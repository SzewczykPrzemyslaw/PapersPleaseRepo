package com.example.challenge;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InspectorTest {

    static Inspector inspector;

    @org.junit.jupiter.api.BeforeAll
    public static void setup(){
        inspector = new Inspector();
    }

    private static Stream<Arguments> bulletinExamples(){
        return Stream.of(
                Arguments.of(mapCreator(new String[]{"Arstotzka", "Impor", "Kolechia", "Obristan"},
                                        new String[]{"Antegria", "Republia", "United Federation"},
                                        new String[]{"Kristof Dimitrov"},
                                        new String[]{"polio vaccination"}, //arstotzka
                                        new String[]{"polio vaccination", "typhus vaccination"}, //antegria
                                        new String[]{"polio vaccination", "yellow fever vaccination"}, //impor
                                        new String[]{}, //kolechia
                                        new String[]{"polio vaccination", "yellow fever vaccination"}, //obristan
                                        new String[]{"polio vaccination"}, //republia
                                        new String[]{"polio vaccination"}), Inspector.getNewBulletin()) //united Federation
        );
    }

    private static Map<String, Set<String>> mapCreator(String[] allowedEntries, String[] deniedEntries, String[] wantedEntries, String[] arstotzkaEntries,
                                                       String[] antegriaEntries, String[] imporEntries, String[] kolechiaEntries, String[] obristanEntries,
                                                       String[] republiaEntries, String[] unitedFederationEntries){
        Set<String> allowed = Set.of(allowedEntries);
        Set<String> denied = Set.of(deniedEntries);
        Set<String> wanted = Set.of(wantedEntries);
        Set<String> arstotzka = Set.of(arstotzkaEntries);
        Set<String> antegria = Set.of(antegriaEntries);
        Set<String> impor = Set.of(imporEntries);
        Set<String> kolechia = Set.of(kolechiaEntries);
        Set<String> obristan = Set.of(obristanEntries);
        Set<String> republia = Set.of(republiaEntries);
        Set<String> unitedFederation = Set.of(unitedFederationEntries);
        return Map.ofEntries(Map.entry("Allowed", allowed), Map.entry("Denied", denied),
                Map.entry("Arstotzka", arstotzka), Map.entry("Antegria", antegria), Map.entry("Impor", impor),
                Map.entry("Kolechia", kolechia), Map.entry("Obristan", obristan), Map.entry("Republia", republia),
                Map.entry("UnitedFederation", unitedFederation), Map.entry("Wanted", wanted));
    }

    @ParameterizedTest
    @MethodSource("bulletinExamples")
    void receiveBulletin(Map<String, Set<String>> input, Map<String, Set<String>> output) {
        String bulletin = "Allow citizens of Arstotzka, Impor, Kolechia\n" +
                "Deny citizens of Antegria, United Federation\n" +
                "Foreigners require access permit\n" +
                "Entrants require polio vaccination\n" +
                "Citizens of Antegria require typhus vaccination\n" +
                "Workers require work pass\n" +
                "Citizens of Arstotzka require ID card\n" +
                "Wanted by the State: Sven Atreides";

        String bulletin_2 = "Allow citizens of Obristan, Antegria\n" +
                "Deny citizens of Republia, Impor\n" +
                "Foreigners require hepatitis B vaccination\n" +
                "Citizens of Obristan, Impor require yellow fever vaccination\n" +
                "Wanted by the State: Zera Wagner";

        String bulletin_3 = "Allow citizens of Impor\n" +
                "Deny citizens of Antegria\n" +
                "Citizens of Kolechia no longer require polio vaccination\n" +
                "Foreigners no longer require hepatitis B vaccination\n" +
                "Wanted by the State: Kristof Dimitrov";
        inspector.receiveBulletin(bulletin);
        inspector.receiveBulletin(bulletin_2);
        inspector.receiveBulletin(bulletin_3);
        assertEquals(input, output);
    }

    @ParameterizedTest
    @MethodSource("inspectExamples")
    void inspect(String input, String output){
        assertEquals(input, output);
    }

    private static Stream<Arguments> inspectExamples(){
        Map<String, String> cameron = new HashMap<>();
        cameron.put("work_pass", "NAME: Lukowski, Cameron\n" + "FIELD: Food service\n" + "EXP: 1984.08.17");
        cameron.put("passport", "NATION: Kolechia\nDOB: 1955.02.18\nSEX: F\nISS: Lesrenadi\nID#: ZDP8Q-SARYS\nEXP: 1985.03.04\nNAME: Lukowski, Cameron");
        cameron.put("certificate_of_vaccination", "NAME: Lukowski, Cameron\nID#: ZDP8Q-SARYS\nVACCINES: cholera, polio, HPV");
        cameron.put("access_permit", "NAME: Lukowski, Cameron\nNATION: Kolechia\nID#: ZDP8Q-SARYS\nPURPOSE: WORK\nDURATION: 2 MONTHS\nHEIGHT: 186.0cm\nWEIGHT: 100.0kg\nEXP: 1984.11.30");

        Map<String, String> roman = new HashMap<>();
        roman.put("passport", "ID#: WK9XA-LKM0Q\nNATION: United Federation\nNAME: Dolanski, Roman\nDOB: 1933.01.01\nSEX: M\nISS: Shingleton\nEXP: 1983.05.12");
        roman.put("grant_of_asylum", "NAME: Dolanski, Roman\nNATION: United Federation\nID#: Y3MNC-TPWQ2\nDOB: 1933.01.01\nHEIGHT: 176cm\nWEIGHT: 71kg\nEXP: 1983.09.20");

        Map<String, String> natasha = new HashMap<>();
        natasha.put("passport", "ID#: E5WEQ-M0C3E\nNATION: Arstotzka\nNAME: Young, Natasha\nDOB: 1935.08.11\nSEX: F\nISS: Shingleton\nEXP: 1983.05.12");
        natasha.put("grant_of_asylum", "NAME: Young, Natasha\nNATION: Arstotzka\nID#: E5WEQ-M0C3E\nDOB: 1935.08.11\nHEIGHT: 156cm\nWEIGHT: 51kg\nEXP: 1984.04.23");
        natasha.put("certificate_of_vaccination", "NAME: Young, Natasha\nID#: E5WEQ-M0C3E\nVACCINES: cholera, polio");

        Map<String, String> josef = new HashMap<>();
        josef.put("passport", "ID#: GC07D-FU8AR\nNATION: Arstotzka\nNAME: Costanza, Josef\nDOB: 1933.11.28\nSEX: M\nISS: East Grestin\nEXP: 1983.03.15");
        josef.put("certificate_of_vaccination", "NAME: Costanza, Josef\nID#: GC07D-FU8AR\nVACCINES: cholera, polio, HPV");
        josef.put("ID_card", "NAME: Costanza, Josef\nDOB: 1919.10.22\nHEIGHT: 184cm\nWEIGHT: 96kg");

        Map<String, String> aleksi = new HashMap<>();
        aleksi.put("passport", "NATION: Republia\nDOB: 1926.09.10\nSEX: M\nISS: Tsunkeido\nID#: IS26M-NDJ2S\nEXP: 1983.05.25\nNAME: Gregorovich, Aleksi");
        aleksi.put("certificate_of_vaccination", "NAME: Gregorovich, Aleksi\nID#: IS26M-NDJ2S\nVACCINES: polio, hepatitis B, tetanus");

        Map<String, String> kristof = new HashMap<>();
        kristof.put("access_permit", "NAME: Dimitrov, Kristof\nNATION: Obristan\nID#: YEMP6-L28WT\nPURPOSE: WORK\nDURATION: 3 MONTHS\nHEIGHT: 180.0cm\nWEIGHT: 91.0kg\nEXP: 1985.09.26");
        kristof.put("certificate_of_vaccination", "NAME: Dimitrov, Kristof\nID#: YEMP6-L28WT\nVACCINES: yellow fever, hepatitis B, typhus");

        Map<String, String> roberta = new HashMap<>();
        roberta.put("diplomatic_authorization", "NATION: Kolechia\nNAME: Kierkgaard, Joseph\nID#: BYZOH-RNT90\nACCESS: Arstotzka, Antegria, Impor");

        Map<String, String> zachary = new HashMap<>();
        zachary.put("work_pass", "NAME: Tjell, Zachary\nFIELD: Healthcare\nEXP: 1983.10.13");
        zachary.put("passport", "NATION: Republia\nDOB: 1917.08.03\nSEX: M\nISS: Lesrenadi\nID#: O6MR2-UQQVX\nEXP: 1984.04.03\nNAME: Tjell, Zachary\n");
        zachary.put("access_permit", "access_permit\nNAME: Tjell, Zachary\nNATION: Republia\nID#: O6MR2-UQQVX\nPURPOSE: WORK\nDURATION: 1 YEAR\nHEIGHT: 155.0cm\nWEIGHT: 55.0kg\nEXP: 1985.09.18\n");

        Map<String, String> karin = new HashMap<>();
        karin.put("passport", "ID#: ED9XY-BKM0Q\nNATION: Impor\nNAME: Dahl, Karin\nDOB: 1933.01.01\nSEX: M\nISS: Shingleton\nEXP: 1983.03.16");
        karin.put("access_permit", "NAME: Pearl, Omid\nNATION: Impor\nID#: ED9XY-BKM0Q\nDURATION: 3 MONTHS\nHEIGHT: 170.0cm\nWEIGHT: 61.0kg\nEXP: 1984.09.26");

        Map<String, String> lisa = new HashMap<>();
        lisa.put("passport", "ID#: ED9XY-BKM0Q\nNATION: Kolechia\nNAME: Mikkelson, Lisa\nDOB: 1933.01.01\nSEX: M\nISS: Shingleton\nEXP: 1983.03.16");

        Map<String, String> sofia = new HashMap<>();
        sofia.put("passport", "NATION: Kolechia\nDOB: 1936.01.29\nSEX: F\nISS: Tsunkeido\nID#: FU1RU-GYFI8\nNAME: Rasmussen, Sofia\nEXP: 1983.03.16");
        sofia.put("diplomatic_authorization", "NATION: Kolechia\nNAME: Rasmussen, Sofia\nID#: FU1RU-GYFI8\nACCESS: Antegria, Impor, Republia");

        return Stream.of(
                Arguments.of(inspector.inspect(cameron), "Cause no trouble."),
                Arguments.of(inspector.inspect(roman), "Detainment: ID number mismatch."),
                Arguments.of(inspector.inspect(natasha), "Entry denied: missing required ID card."),
                Arguments.of(inspector.inspect(josef), "Glory to Arstotzka."),
                Arguments.of(inspector.inspect(aleksi), "Entry denied: citizen of banned nation."),
                Arguments.of(inspector.inspect(kristof), "Detainment: Entrant is a wanted criminal."),
                Arguments.of(inspector.inspect(roberta), "Cause no trouble."),
                Arguments.of(inspector.inspect(zachary), "Entry denied: missing required certificate of vaccination."),
                Arguments.of(inspector.inspect(karin), "Detainment: name mismatch."),
                Arguments.of(inspector.inspect(lisa), "Entry denied: missing required access permit."),
                Arguments.of(inspector.inspect(sofia), "Entry denied: invalid diplomatic authorization.")
        );
    }

    @org.junit.jupiter.api.Test
    void setRequire_IdCard() {
        inspector.setRequire_IdCard(true);
        assertTrue(inspector.getIdCard());
    }

    @org.junit.jupiter.api.Test
    void setRequire_AccessPermit() {
        inspector.setRequire_AccessPermit(true);
        assertTrue(inspector.getAccessPermit());
    }

    @org.junit.jupiter.api.Test
    void setRequire_Passport() {
        inspector.setRequire_Passport(true);
        assertTrue(inspector.getPassport());
    }

    @org.junit.jupiter.api.Test
    void setRequire_WorkPass() {
        inspector.setRequire_WorkPass(true);
        assertTrue(inspector.getWorkPass());
    }

}