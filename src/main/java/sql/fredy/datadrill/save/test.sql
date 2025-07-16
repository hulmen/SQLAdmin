SELECT distinct 
org.kundennummer "Kundennummer ",
org.name "organisationsname ",
org.namezusatz "namezusatz ",
org.sprache "sprache ",
org.anzahl_mitarbeiter "mitarbeiter ",
org.telefon "org_telefon ",
org.telefax "org_telefax ",
org.email "org_email ",
org.url "url ",
org.strasse "strasse ",
org.haus_nr "haus_nr ",
org.plz "plz ",
org.ort "ort ",
org.postfach "postfach ",
org.landteil "landteil ",
org.land "Land ",
kont.postanschrift "postanschrift ",
NVL(kont.briefanrede,org.briefanrede) "briefanrede ",
kont.name "name_kontakt ",
kont.vorname "vorname_kontakt ",
kont.telefon "telefon_kontakt ",
kont.natel "natel_kontakt ",
kont.telefax "fax_kontakt ",
kont.email "email_kontakt "
FROM   ch_organisationen_v org
LEFT OUTER JOIN (
     SELECT kp.organisation_oid,
            kp.abteilung,
            kp.postanschrift,
            kp.briefanrede,
            kp.name,
            kp.vorname,
            kp.telefon,
            kp.telefax,
            kp.email,
            kpf.funktion_oid
     FROM kontaktpersonen_v kp INNER JOIN kontaktpers_funktion kpf
            ON   (    kp.kontaktpers_oid = kpf.person_oid 
                  AND kp.organisation_oid = kpf.organisation_oid
                  AND kp.funktion_cd  = IN ('GESCHAEFTSLEITUNG')
                  AND kpf.person_oid = get_kperson_oid_byfunction(kpf.organisation_oid, 'GESCHAEFTSLEITUNG'))
) kont ON org.partner_oid = kont.organisation_oid
INNER JOIN (
SELECT 	partner_oid
,		vsaart
,		vertreter_partner_oid
,		landgruppe
,		land
 FROM   vsa
 WHERE  land IN ('Bundesrepublik Deutschland' )
 AND    vsaart IN ('Vertreter' ) ) USING ( partner_oid )
 INNER JOIN (
  SELECT  partner_oid
  FROM    produkte_v
  WHERE   SUBSTR(klassifikation_nr,1,4) BETWEEN '0563' AND '0577'
  OR      SUBSTR(klassifikation_nr,1,4) BETWEEN '0601' AND '0651'
  OR      SUBSTR(klassifikation_nr,1,4) BETWEEN '0691' AND '0723'
  OR      SUBSTR(klassifikation_nr,1,4) BETWEEN '0734' AND '0803'
  OR      SUBSTR(klassifikation_nr,1,4) BETWEEN '0901' AND '0902'
  OR      SUBSTR(klassifikation_nr,1,4) IN ('0817')  ) USING ( partner_oid )
