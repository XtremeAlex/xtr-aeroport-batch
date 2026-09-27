#!/usr/bin/env python3
"""
Generatore dell'artefatto dati SQLite read-only consumato da xtr-aeroport-api.

Eseguito OFFLINE (una tantum) su un PC potente: legge i dataset JSON e produce
aeroport.sqlite con lo schema atteso dall'API (indici inclusi). Il file va poi
copiato sul Raspberry Pi e montato read-only nel container dell'API.

Uso:
    python3 tools/build-sqlite.py \
        --airports src/main/resources/dataset/airports/world-airport.json \
        --countries src/main/resources/dataset/country/countries-flag.json \
        --out target/aeroport.sqlite
"""
import argparse
import json
import os
import sqlite3
import sys

# ISO 3166-1 alpha-2 -> alpha-3 (necessario per il lookup EDIFACT NAT). Sottoinsieme esteso.
ALPHA2_TO_ALPHA3 = {
    "AF":"AFG","AL":"ALB","DZ":"DZA","AD":"AND","AO":"AGO","AR":"ARG","AM":"ARM","AU":"AUS","AT":"AUT","AZ":"AZE",
    "BS":"BHS","BH":"BHR","BD":"BGD","BB":"BRB","BY":"BLR","BE":"BEL","BZ":"BLZ","BJ":"BEN","BT":"BTN","BO":"BOL",
    "BA":"BIH","BW":"BWA","BR":"BRA","BN":"BRN","BG":"BGR","BF":"BFA","BI":"BDI","KH":"KHM","CM":"CMR","CA":"CAN",
    "CV":"CPV","CF":"CAF","TD":"TCD","CL":"CHL","CN":"CHN","CO":"COL","KM":"COM","CG":"COG","CD":"COD","CR":"CRI",
    "CI":"CIV","HR":"HRV","CU":"CUB","CY":"CYP","CZ":"CZE","DK":"DNK","DJ":"DJI","DM":"DMA","DO":"DOM","EC":"ECU",
    "EG":"EGY","SV":"SLV","GQ":"GNQ","ER":"ERI","EE":"EST","ET":"ETH","FJ":"FJI","FI":"FIN","FR":"FRA","GA":"GAB",
    "GM":"GMB","GE":"GEO","DE":"DEU","GH":"GHA","GR":"GRC","GD":"GRD","GT":"GTM","GN":"GIN","GW":"GNB","GY":"GUY",
    "HT":"HTI","HN":"HND","HU":"HUN","IS":"ISL","IN":"IND","ID":"IDN","IR":"IRN","IQ":"IRQ","IE":"IRL","IL":"ISR",
    "IT":"ITA","JM":"JAM","JP":"JPN","JO":"JOR","KZ":"KAZ","KE":"KEN","KI":"KIR","KP":"PRK","KR":"KOR","KW":"KWT",
    "KG":"KGZ","LA":"LAO","LV":"LVA","LB":"LBN","LS":"LSO","LR":"LBR","LY":"LBY","LI":"LIE","LT":"LTU","LU":"LUX",
    "MG":"MDG","MW":"MWI","MY":"MYS","MV":"MDV","ML":"MLI","MT":"MLT","MH":"MHL","MR":"MRT","MU":"MUS","MX":"MEX",
    "FM":"FSM","MD":"MDA","MC":"MCO","MN":"MNG","ME":"MNE","MA":"MAR","MZ":"MOZ","MM":"MMR","NA":"NAM","NR":"NRU",
    "NP":"NPL","NL":"NLD","NZ":"NZL","NI":"NIC","NE":"NER","NG":"NGA","NO":"NOR","OM":"OMN","PK":"PAK","PW":"PLW",
    "PA":"PAN","PG":"PNG","PY":"PRY","PE":"PER","PH":"PHL","PL":"POL","PT":"PRT","QA":"QAT","RO":"ROU","RU":"RUS",
    "RW":"RWA","KN":"KNA","LC":"LCA","VC":"VCT","WS":"WSM","SM":"SMR","ST":"STP","SA":"SAU","SN":"SEN","RS":"SRB",
    "SC":"SYC","SL":"SLE","SG":"SGP","SK":"SVK","SI":"SVN","SB":"SLB","SO":"SOM","ZA":"ZAF","SS":"SSD","ES":"ESP",
    "LK":"LKA","SD":"SDN","SR":"SUR","SE":"SWE","CH":"CHE","SY":"SYR","TW":"TWN","TJ":"TJK","TZ":"TZA","TH":"THA",
    "TL":"TLS","TG":"TGO","TO":"TON","TT":"TTO","TN":"TUN","TR":"TUR","TM":"TKM","TV":"TUV","UG":"UGA","UA":"UKR",
    "AE":"ARE","GB":"GBR","US":"USA","UY":"URY","UZ":"UZB","VU":"VUT","VE":"VEN","VN":"VNM","YE":"YEM","ZM":"ZMB",
    "ZW":"ZWE",
}

SCHEMA = """
CREATE TABLE A_T_AIRPORT_TYPE (id INTEGER PRIMARY KEY, name TEXT NOT NULL, description TEXT);
CREATE TABLE A_T_COUNTRY (id INTEGER PRIMARY KEY, iso_alpha2 TEXT NOT NULL, iso_alpha3 TEXT, name TEXT NOT NULL, description TEXT);
CREATE TABLE A_D_AIRPORT (
    id INTEGER PRIMARY KEY, iata_code TEXT, icao_code TEXT, ident TEXT, name TEXT NOT NULL,
    municipality TEXT, continent TEXT, coordinates TEXT, elevation_ft INTEGER, gps_code TEXT,
    iso_country TEXT, iso_region TEXT, local_code TEXT, id_airport_type INTEGER
);
CREATE TABLE E_D_DOCUMENT_TYPE (id INTEGER PRIMARY KEY, edifact_code TEXT NOT NULL, name TEXT NOT NULL);
CREATE INDEX idx_airport_iata ON A_D_AIRPORT(iata_code);
CREATE UNIQUE INDEX idx_airport_icao ON A_D_AIRPORT(icao_code) WHERE icao_code IS NOT NULL;
CREATE INDEX idx_airport_ident ON A_D_AIRPORT(ident);
CREATE INDEX idx_airport_iso_country ON A_D_AIRPORT(iso_country);
CREATE INDEX idx_airport_type ON A_D_AIRPORT(id_airport_type);
CREATE UNIQUE INDEX idx_country_alpha2 ON A_T_COUNTRY(iso_alpha2);
CREATE UNIQUE INDEX idx_country_alpha3 ON A_T_COUNTRY(iso_alpha3) WHERE iso_alpha3 IS NOT NULL;
CREATE UNIQUE INDEX idx_doc_edifact ON E_D_DOCUMENT_TYPE(edifact_code);
"""

DOCUMENT_TYPES = [
    (1, "P", "Passaporto"),
    (2, "I", "Carta d'identità"),
    (3, "V", "Visto"),
    (4, "A", "Documento equivalente al passaporto"),
    (5, "C", "Carta"),
    (6, "F", "Documento familiare"),
]


def fix_mojibake(s):
    """Corregge testo UTF-8 erroneamente codificato come Latin-1 (es. 'â\x80\x93' -> '–')."""
    if not s:
        return s
    try:
        return s.encode("latin-1").decode("utf-8")
    except (UnicodeEncodeError, UnicodeDecodeError):
        return s


def to_int(v):
    try:
        return int(str(v).strip())
    except (TypeError, ValueError):
        return None


def build(airports_path, countries_path, out_path):
    with open(airports_path, encoding="utf-8") as f:
        airports = json.load(f)
    with open(countries_path, encoding="utf-8") as f:
        countries = json.load(f)

    os.makedirs(os.path.dirname(out_path) or ".", exist_ok=True)
    if os.path.exists(out_path):
        os.remove(out_path)

    con = sqlite3.connect(out_path)
    cur = con.cursor()
    cur.executescript(SCHEMA)

    # Tipi aeroporto (id derivato dall'ordine dei tipi distinti)
    types = sorted({a.get("type") for a in airports if a.get("type")})
    type_id = {name: i + 1 for i, name in enumerate(types)}
    cur.executemany("INSERT INTO A_T_AIRPORT_TYPE(id,name,description) VALUES (?,?,?)",
                    [(i, name, None) for name, i in type_id.items()])

    # Paesi (alpha2 dal dataset, alpha3 dalla mappa ISO)
    country_rows = []
    for i, c in enumerate(countries, start=1):
        a2 = (c.get("Code") or "").strip().upper()
        if not a2:
            continue
        country_rows.append((i, a2, ALPHA2_TO_ALPHA3.get(a2), fix_mojibake(c.get("Name")), None))
    cur.executemany("INSERT INTO A_T_COUNTRY(id,iso_alpha2,iso_alpha3,name,description) VALUES (?,?,?,?,?)",
                    country_rows)

    # Aeroporti
    seen_icao = set()
    rows = []
    for i, a in enumerate(airports, start=1):
        ident = (a.get("ident") or "").strip() or None
        icao = ident if ident and len(ident) == 4 else None
        # garantisce univocità dell'icao (l'indice è UNIQUE)
        if icao and icao in seen_icao:
            icao = None
        if icao:
            seen_icao.add(icao)
        rows.append((
            i,
            (a.get("iata_code") or None),
            icao,
            ident,
            fix_mojibake(a.get("name")) or "N/A",
            fix_mojibake(a.get("municipality")),
            a.get("continent"),
            a.get("coordinates"),
            to_int(a.get("elevation_ft")),
            a.get("gps_code"),
            a.get("iso_country"),
            a.get("iso_region"),
            a.get("local_code"),
            type_id.get(a.get("type")),
        ))
    cur.executemany(
        "INSERT INTO A_D_AIRPORT(id,iata_code,icao_code,ident,name,municipality,continent,"
        "coordinates,elevation_ft,gps_code,iso_country,iso_region,local_code,id_airport_type) "
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", rows)

    cur.executemany("INSERT INTO E_D_DOCUMENT_TYPE(id,edifact_code,name) VALUES (?,?,?)", DOCUMENT_TYPES)

    con.commit()
    # ottimizza il file read-only
    cur.execute("ANALYZE")
    cur.execute("VACUUM")
    con.commit()

    # report
    for tbl in ("A_D_AIRPORT", "A_T_COUNTRY", "A_T_AIRPORT_TYPE", "E_D_DOCUMENT_TYPE"):
        n = cur.execute(f"SELECT count(*) FROM {tbl}").fetchone()[0]
        print(f"  {tbl}: {n} righe")
    con.close()
    size_mb = os.path.getsize(out_path) / (1024 * 1024)
    print(f"Creato {out_path} ({size_mb:.1f} MB)")


def main():
    p = argparse.ArgumentParser(description="Genera aeroport.sqlite per xtr-aeroport-api")
    p.add_argument("--airports", required=True)
    p.add_argument("--countries", required=True)
    p.add_argument("--out", default="target/aeroport.sqlite")
    args = p.parse_args()
    build(args.airports, args.countries, args.out)
    return 0


if __name__ == "__main__":
    sys.exit(main())
