# Zadáni na endpointy

## Get/ičo

### Požadavek

vytvořit get endpoint, který bude vracet souhrn informaci o zadané firmě

### Zadání

get endpoint, ve kterém se bude specifikovat hledané ičo

#### vstup

- get/`ico`: [int]

#### logika

  1) zkontrolovat jestli je záznam s takovým ičo (z url) uložený v databázi
      1) ano - zkontrolovat, jestli updated je menši, než 1 týden
          1) ano - vrati se detail firmy
          2) ne - udělá se dotaz do ares
              1) pokud ares zahlasi chybu, pošle se stejný error, jako z aresu
                  1) pokud je to chyba s kódem 400 - záznam se smaže (není v aresu)
              2) pokud ares vratil firmu, přepíše se záznam s tímto ičo podle tabulky a zpět se pošle detail firmy
      2) ne - udělá se dotaz do ares
          1) pokud ares zahlasi chybu, pošle se stejný error, jako z aresu
              1) pokud je to chyba s kódem 400 - záznam se smaže (není v aresu)
          2) pokud ares vratil firmu, zapíše se záznam s tímto ičo podle tabulky a zpět se pošle detail firmy

#### tabulka

název tabulky: `companies`

- ico [int(pk)]: z api `ico`
- name [string]: z api `obchodniJmeno`
- address [string]: z api `sidlo.textovaAdresa`
- updated [datetime]: vygeneruje se při natažení z aresu

#### výstup

```json
{
    "ico":"companies.ico",
    "name":"companies.name",
    "address":"companies.address",
    "updated":"companies.updated"
}
```

## Post

### Požadavek

vytvořit post endpoint, pro vyhledavání firem. Ať už uložených, nebo z aresu

### Zadání

post endpoint, který bude dle atributu v body hledat v databázi a aresu

#### vstup

- body: *vždy musí obsahovat aspoň jeden z name/address

```json
{
    "name": "string",
    "address": "string",
    "onlySaved": "bool"
}
```

#### logika

- `onlySaved` = true
    - hledat se bude jen v záznamech uložených v databázi
- `onlySaved` = false/null
    - hledat se bude v ares a pokud záznam s ičo z aresu je v databázi, updatne se

- hledání v databázi
    - name, address
        - oseknou se háčky, čárky, převede se na lowcase
            - zkontroluje jestli odpovídající sloupce v databázi obsahují této osekáné vstupy
            - ziskane záznamy se pošlou v seznamu dle `výstup`
- hledání v ares
    - pošle se post na ares endpoint `/ekonomicke-subjekty/vyhledat`
    - s body:

   ```json
   {
      "pocet": 100,
      "obchodniJmeno": "{name}",
      "sidlo": {
         "textovaAdresa": "{address}"
      }
   }
   ```

    - ziskane záznamy se zkontrolují proti databázi
        - pokud existuje, ičo v databázi updatne se záznam (přepíše se podle dat z ares)
            - pokud neexistuje, založí se nový záznam, dle mapování v `tabulka`
    - tyto nové/upravené záznamy se pošlou v seznamu dle `výstup`

#### tabulka

název tabulky: `companies`

- ico [int(pk)]: z api `ekonomickeSubjekty[*].ico`
- name [string]: z api `ekonomickeSubjekty[*].obchodniJmeno`
- address [string]: z api `ekonomickeSubjekty[*].sidlo.textovaAdresa`
- updated [datetime]: vygeneruje se při natažení z aresu

#### výstup

```json
[
   {
      "ico":"companies.ico",
      "name":"companies.name",
      "address":"companies.address",
      "updated":"companies.updated"
   }
]
```
