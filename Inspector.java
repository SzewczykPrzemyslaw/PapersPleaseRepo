package com.example.challenge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Inspector {
    private static Set<String> allowed = new HashSet<>();
    private static Set<String> denied = new HashSet<>();
    private static Set<String> wanted = new HashSet<>();
    private static Set<String> arstotzka = new HashSet<>();
    private static Set<String> antegria = new HashSet<>();
    private static Set<String> impor = new HashSet<>();
    private static Set<String> kolechia = new HashSet<>();
    private static Set<String> obristan = new HashSet<>();
    private static Set<String> republia = new HashSet<>();
    private static Set<String> unitedFederation = new HashSet<>();

    private static final List<Set<String>> countries = List.of(arstotzka, antegria, impor, kolechia, obristan, republia, unitedFederation);
    private static final List<Set<String>> foreigners = List.of(antegria, impor, kolechia, obristan, republia, unitedFederation);

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd");
    private static final String expirationDate = "1982.11.22";

    List<String> documentNames = new ArrayList<>();
    List<String> documentIds = new ArrayList<>();
    List<String> documentNations = new ArrayList<>();
    List<String> decisionData = new ArrayList<>();

    private boolean require_IdCard = false;
    private boolean require_AccessPermit = false;
    private boolean require_Passport = false;
    private boolean require_WorkPass = false;

    private static Map<String, Set<String>> newBulletin = Map.of("Allowed", allowed, "Denied", denied, "Arstotzka", arstotzka,
            "Antegria", antegria, "Impor", impor, "Kolechia", kolechia, "Obristan", obristan, "Republia", republia,
            "UnitedFederation", unitedFederation, "Wanted", wanted);

    private static final String requireRegEx = ".*require ";
    private static final String ofRegEx = ".*of ";
    private static final String colonRegEx = ".*: ";

    private static boolean checkIfMatches(String bulletin, String containRegEx){
        String[] splitBulletin = bulletin.split("\\n");
        return Arrays.stream(splitBulletin)
                .anyMatch(s -> s.matches(containRegEx));
    }

    private static List<String> searchForCountry(String bulletin, String containRegEx, String replaceRegEx){
        String[] splitBulletin = bulletin.split("\\n");
        return Arrays.stream(splitBulletin)
                .filter(s -> s.matches(containRegEx))
                .map(s -> s.replaceAll(replaceRegEx, ""))
                .flatMap( s -> Arrays.stream(s.split(", ")))
                .collect(Collectors.toList());
    }

    private static void loopOverAddCountries(List<Set<String>> listOfCountries, String bulletin, String containRegEx, String replaceRegEx){
        for(Set<String> countries: listOfCountries){
            countries.addAll(searchForCountry(bulletin, containRegEx, replaceRegEx));
        }
    }

    private static void loopOverRemoveCountries(List<Set<String>> listOfCountries, String bulletin, String containRegEx, String replaceRegEx){
        for(Set<String> countries: listOfCountries){
            searchForCountry(bulletin, containRegEx, replaceRegEx).forEach(countries::remove);
        }
    }

    private String citizensOfReplace(String bulletin, String matchRegEx, String replaceRegEx){
        String[] splitBulletin = bulletin.split("\\n");
        for(String s: splitBulletin){
            if(s.matches(matchRegEx)){
                return s.replaceAll(ofRegEx, "").replaceAll(replaceRegEx,"");
            }
        }
        return null;
    }

    private String vaccinationAddReplace(String bulletin){
        String[] splitBulletin = bulletin.split("\\n");
        for(String s: splitBulletin){
            if(s.matches("Citizens of (.+?) require (.+?) vaccination")){
                return s.replaceAll(requireRegEx,"");
            }
        }
        return null;
    }

    private String vaccinationRemoveReplace(String bulletin){
        String[] splitBulletin = bulletin.split("\\n");
        for(String s: splitBulletin){
            if(s.matches(("Citizens of (.+?) no longer require (.+?) vaccination"))){
                return s.replaceAll(requireRegEx, "");
            }
        }
        return null;
    }

    private void AddOrRemoveVaccination(String bulletin, String matchRegEx, String replaceRegEx){
        String countriesString = citizensOfReplace(bulletin, matchRegEx, replaceRegEx); // getting countries to string
        String vaccinationAdd = vaccinationAddReplace(bulletin); // getting vaccination to string
        String vaccinationRemove = vaccinationRemoveReplace(bulletin); // getting vaccination to string
        try{
            if(countriesString != null && countriesString.matches(".*,.*")){
                String[] split = countriesString.split(", ");
                for(String s: split){
                    String string = s.replaceAll("\\s", "");
                    if(matchRegEx.matches(".*no longer.*")){
                        newBulletin.get(string).remove(vaccinationRemove);
                    } else {
                        newBulletin.get(string).add(vaccinationAdd);
                    }
                }
            } else {
                String string = countriesString.replaceAll("\\s", "");
                if(matchRegEx.matches(".*no longer.*")){
                    newBulletin.get(string).remove(vaccinationRemove);
                } else {
                    newBulletin.get(string).add(vaccinationAdd);
                }
            }
        } catch(NullPointerException e){}
    }

    public void receiveBulletin(String bulletin) {
        System.out.println(bulletin);
        if(checkIfMatches(bulletin, "Allow citizens of (.+)")){
            allowed.addAll(searchForCountry(bulletin, "Allow citizens of (.+)", ofRegEx));
            searchForCountry(bulletin, "Allow citizens of (.+)", ofRegEx).forEach(denied::remove);
        }
        if(checkIfMatches(bulletin, "Deny citizens of (.+)")){
            denied.addAll(searchForCountry(bulletin, "Deny citizens of (.+)", ofRegEx));
            searchForCountry(bulletin, "Deny citizens of (.+)", ofRegEx).forEach(allowed::remove);
        }
        if(checkIfMatches(bulletin, "Foreigners require access permit")){
            setRequire_AccessPermit(true);
        }
        if(checkIfMatches(bulletin, "Citizens of Arstotzka require ID card")){
            setRequire_IdCard(true);
        }
        if(checkIfMatches(bulletin, "Entrants require passport")){
            setRequire_Passport(true);
        }
        if(checkIfMatches(bulletin, "Workers require work pass")){
            setRequire_WorkPass(true);
        }
        if(checkIfMatches(bulletin, "Foreigners require (.+?) vaccination")){
            loopOverAddCountries(foreigners, bulletin, "Foreigners require (.+?) vaccination", requireRegEx);
        }
        if(checkIfMatches(bulletin, "Foreigners no longer require (.+?) vaccination")){
            loopOverRemoveCountries(foreigners, bulletin, "Foreigners no longer require (.+?) vaccination", requireRegEx);
        }
        if(checkIfMatches(bulletin, "Entrants require (.+?) vaccination")){
            loopOverAddCountries(countries, bulletin, "Entrants require (.+?) vaccination", requireRegEx);
        }
        if(checkIfMatches(bulletin, "Entrants no longer require (.+?) vaccination")){
            loopOverRemoveCountries(countries, bulletin, "Entrants no longer require (.+?) vaccination", requireRegEx);
        }
        if(checkIfMatches(bulletin, "Citizens of (.+?) require (.+?) vaccination")){
            AddOrRemoveVaccination(bulletin, "Citizens of (.+?) require (.+?) vaccination", " require.*");
        }
        if(checkIfMatches(bulletin, "Citizens of (.+?) no longer require (.+?) vaccination")){
            AddOrRemoveVaccination(bulletin, "Citizens of (.+?) no longer require (.+?) vaccination", " no longer.*");
        }
        if(checkIfMatches(bulletin, "Wanted by the State: (.+)")){
            wanted = newBulletin.get("Wanted");
            wanted.clear();
            wanted.addAll(searchForCountry(bulletin, "Wanted by the State: (.+)", colonRegEx));
        }
    }

    public String inspect(Map<String, String> person) {
        decisionData.clear();

        if(checkCountry(person, "Arstotzka")){
            inspectVaccination(person, arstotzka);
            inspectDataEquality(person);
            inspectIfAllowed(person, allowed, "NATION", "Arstotzka");
            inspectIfWanted(person);
            inspectIfDocExpired(person);
            inspectID(person);
            return makeDecision(decisionData, person);
        }
        if(checkCountry(person, "Antegria")){
            inspectVaccination(person, antegria);
            inspectDataEquality(person);
            inspectIfAllowed(person, allowed, "NATION", "Antegria");
            inspectIfWanted(person);
            inspectIfDocExpired(person);
            inspectID(person);
            return makeDecision(decisionData, person);
        }
        if(checkCountry(person, "Impor")){
            inspectVaccination(person, impor);
            inspectDataEquality(person);
            inspectIfAllowed(person, allowed, "NATION", "Impor");
            inspectIfWanted(person);
            inspectIfDocExpired(person);
            inspectID(person);
            return makeDecision(decisionData, person);
        }
        if(checkCountry(person, "Kolechia")){
            inspectVaccination(person, kolechia);
            inspectDataEquality(person);
            inspectIfAllowed(person, allowed, "NATION", "Kolechia");
            inspectIfWanted(person);
            inspectIfDocExpired(person);
            inspectID(person);
            return makeDecision(decisionData, person);
        }
        if(checkCountry(person, "Obristan")){
            inspectVaccination(person, obristan);
            inspectDataEquality(person);
            inspectIfAllowed(person, allowed, "NATION", "Obristan");
            inspectIfWanted(person);
            inspectIfDocExpired(person);
            inspectID(person);
            return makeDecision(decisionData, person);
        }
        if(checkCountry(person, "Republia")){
            inspectVaccination(person, republia);
            inspectDataEquality(person);
            inspectIfAllowed(person, allowed, "NATION", "Republia");
            inspectIfWanted(person);
            inspectIfDocExpired(person);
            inspectID(person);
            return makeDecision(decisionData, person);
        }
        if(checkCountry(person, "United Federation")){
            inspectVaccination(person, unitedFederation);
            inspectDataEquality(person);
            inspectIfAllowed(person, allowed, "NATION", "United Federation");
            inspectIfWanted(person);
            inspectIfDocExpired(person);
            inspectID(person);
            return makeDecision(decisionData, person);
        }
        checkLackOfID(person);
        inspectIfDocExpired(person);
        diplomaticAccess(person);
        return makeDecision(decisionData, person);
    }

    private boolean checkCountry(Map<String, String> person, String country) {
        for (Map.Entry<String, String> entry : person.entrySet()) {
            if (entry.getValue().contains("NATION:") && !entry.getValue().contains("ACCESS:")) {
                String[] splitData = entry.getValue().split("\\n");
                return Arrays.stream(splitData)
                        .anyMatch(s -> s.contains(country));
            }
        }
        return false;
    }

    private void inspectVaccination(Map<String, String> personData, Set<String> country){
        for(Map.Entry<String, String> personEntry : personData.entrySet()){
            for (String vaccinationData : country) {
                if (vaccinationData.matches(".*vaccination.*") && !personData.containsKey("certificate_of_vaccination")) {
                    decisionData.add("Entry denied: missing required certificate of vaccination.");
                }
                if ("certificate_of_vaccination".equals(personEntry.getKey()) && vaccinationData.matches(".*vaccination.*")) {
                    String require = vaccinationData.replaceAll(" vaccination", "").replaceAll("_", " ");
                    if (!personEntry.getValue().contains(require)) {
                        decisionData.add("Entry denied: missing required vaccination.");
                    }
                }
            }
        }
    }

    private void inspectDataEquality(Map<String, String> personData){
        for(Map.Entry<String, String> personEntry : personData.entrySet()){
            String[] splitData = personEntry.getValue().split("\\n");
            for (String dataSplit : splitData) {
                if (dataSplit.contains("NAME:")) {
                    documentNames.add(dataSplit.replaceAll("NAME: ", ""));
                }
                if (dataSplit.contains("ID#:")) {
                    documentIds.add(dataSplit.replaceAll("ID#: ", ""));
                }
                if (dataSplit.contains("NATION:")) {
                    documentNations.add(dataSplit.replaceAll("NATION: ", ""));
                }
            }
        }
        if (!verifyEquality(documentNames) || !verifyEquality(documentIds) || !verifyEquality(documentNations)) {
            if (!verifyEquality(documentNames)) {
                decisionData.add("Detainment: name mismatch.");
            }
            if (!verifyEquality(documentIds)) {
                decisionData.add("Detainment: ID number mismatch.");
            }
            if (!verifyEquality(documentNations)) {
                decisionData.add("Detainment: nationality mismatch.");
            }
        }
        documentNations.clear();
        documentNames.clear();
        documentIds.clear();
    }

    private boolean verifyEquality(List<String> list){
        return list.stream()
                .allMatch( s -> s.equals(list.get(0)));
    }

    private void inspectIfAllowed(Map<String, String> personData, Set<String> bulletinRequirements, String lineOfData, String requirement){
        if(!inspectPersonDocument(personData, lineOfData, requirement) || !inspectBulletinRequirement(bulletinRequirements, requirement)){
            decisionData.add("Entry denied: citizen of banned nation.");
        }
    }

    private boolean inspectBulletinRequirement(Set<String> bulletinRequirements, String requirement){
        return bulletinRequirements.stream()
                .anyMatch( s -> s.contains(requirement));
    }

    private boolean inspectPersonDocument(Map<String, String> personDocument, String lineOfDocument, String requirement){
        for(Map.Entry<String, String> personEntry : personDocument.entrySet()) {
            if(!personEntry.getKey().equals("certificate_of_vaccination") && !personEntry.getKey().equals("ID_card") &&
            !personEntry.getKey().equals("work_pass")){
                String[] splitData = personEntry.getValue().split("\\n");
                return Arrays.stream(splitData)
                        .filter(s -> s.contains(lineOfDocument))
                        .anyMatch(s -> s.contains(requirement));
            }
        }
        return false;
    }

    private void inspectIfWanted(Map<String, String> personDocument) {
        for (Map.Entry<String, String> personEntry : personDocument.entrySet()) {
            String[] splitData = personEntry.getValue().split("\\n");
            for (String dataRequirements : splitData) {
                if (dataRequirements.contains("NAME")) {
                    String checkName = dataRequirements.replaceAll(colonRegEx, "");
                    try {
                        String wantedCriminalSecondName = checkName.replaceAll(",.*", "");
                        String wantedCriminalFirstName = checkName.replaceAll(".*, ", "");
                        if (wanted.iterator().next().matches(".*" + wantedCriminalFirstName + ".*") &&
                                wanted.iterator().next().matches(".*" + wantedCriminalSecondName + ".*")) {
                            decisionData.add("Detainment: Entrant is a wanted criminal.");
                        }
                    } catch (NoSuchElementException e) {
                    }
                }
            }
        }
    }
    private void inspectIfDocExpired(Map<String, String> personDocument){
        for(Map.Entry<String, String> personEntry : personDocument.entrySet()) {
            String[] splitData = personEntry.getValue().split("\\n");
            for (String dataRequirements : splitData) {
                if (dataRequirements.contains("EXP")) {
                    String checkDate = dataRequirements.replaceAll(colonRegEx, "");
                    try {
                        Date date = fmt.parse(expirationDate);
                        Date date1 = fmt.parse(checkDate);
                        if (date.compareTo(date1) >= 0) {
                            decisionData.add("Entry denied: " + personEntry.getKey().replaceAll("_", " ") + " expired.");
                        }
                    } catch (ParseException e) {
                        System.out.println("Parse Exception, error has been reached while parsing");
                    }
                }
            }
        }
    }

    private void inspectID(Map<String, String> personDocument){
        if(require_Passport){
            if(!personDocument.containsKey("passport")){
                decisionData.add("Entry denied: missing required passport.");
            }
        }
        if(!checkCountry(personDocument, "Arstotzka")){
            if(require_AccessPermit){
                if(personDocument.containsKey("diplomatic_authorization") && !checkAuthorization(personDocument)){
                    decisionData.add("Entry denied: invalid diplomatic authorization.");
                }
                if(!personDocument.containsKey("access_permit") && !personDocument.containsKey("diplomatic_authorization") &&
                        !personDocument.containsKey("grant_of_asylum")){
                    decisionData.add("Entry denied: missing required access permit.");
                }
            }
        }
        if(checkCountry(personDocument, "Arstotzka")) {
            if (require_IdCard) {
                if (!personDocument.containsKey("ID_card")) {
                    decisionData.add("Entry denied: missing required ID card.");
                }
            }
        }
        if(require_WorkPass){
            inspectWorker(personDocument);
        }
    }

    private boolean checkAuthorization(Map<String, String> personDocument) {
        for (Map.Entry<String, String> entry : personDocument.entrySet()) {
            if (entry.getKey().equals("diplomatic_authorization")) {
                String[] splitData = entry.getValue().split("\\n");
                for (String s : splitData) {
                    if (s.matches(".*ACCESS:.*")) {
                        if (!s.matches(".*Arstotzka.*")) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private String makeDecision(List<String> decisionData, Map<String, String> personDocument){
        if(decisionData.size() == 1){
            return decisionData.get(0);
        }
        if(decisionData.size() > 1){
            if(!searchDetainment(decisionData)){
                return entryDeniedPriority(decisionData);
            }
            if(searchDetainment(decisionData)){
                return detainmentPriority(decisionData);
            }
        }
        return checkCountry(personDocument, "Arstotzka") ? "Glory to Arstotzka." : "Cause no trouble.";
    }

    private boolean searchDetainment(List<String> decisionData){
        return decisionData.stream()
                .anyMatch( s -> s.matches(".*Detainment:.*"));
    }

    private String detainmentPriority(List<String> decisionData){
        String finalDecision = "";
        for(String s : decisionData){
            if(s.matches("Detainment: Entrant is a wanted criminal.")){
                finalDecision = "Detainment: Entrant is a wanted criminal.";
            }
            if(s.matches("Detainment: nationality mismatch.")){
                finalDecision = "Detainment: nationality mismatch.";
            }
            if(s.matches("Detainment: name mismatch.")){
                finalDecision = "Detainment: name mismatch.";
            }
            if(s.matches("Detainment: ID number mismatch.")){
                finalDecision = "Detainment: ID number mismatch.";
            }
        }
        return finalDecision;
    }

    private String entryDeniedPriority(List<String> decisionData){
        for(String denial: decisionData){
            if(denial.matches(".*Entry denied: invalid diplomatic authorization.*")){
                return "Entry denied: invalid diplomatic authorization.";
            }
            if(denial.matches(".*expired.*")){
                return denial;
            }
            if(denial.matches("Entry denied: missing required passport.*")){
                return denial;
            }
        }
        for(String denial: decisionData){
            if(denial.matches("Entry denied: missing required passport.*")){
                return denial;
            }
            if(denial.matches("Entry denied:.*")){
                return denial;
            }
        }
        return decisionData.get(0);
    }

    private void inspectWorker(Map<String, String> person) {
        for(Map.Entry<String, String> entry : person.entrySet()) {
            String[] splitAccessData = entry.getValue().split("\\n");
            for(String data : splitAccessData) {
                if(data.contains("PURPOSE:") && require_WorkPass) {
                    String check = data.replaceAll(colonRegEx, "");
                    if(check.equals("WORK")) {
                        if(!person.containsKey("work_pass")) {
                            decisionData.add("Entry denied: missing required work pass.");
                        }
                    }
                }
            }
        }
    }

    private void checkLackOfID(Map<String, String> personDocument){
        if(personDocument.keySet().size() == 1 && (personDocument.containsKey("certificate_of_vaccination") ||
                personDocument.containsKey("ID_card"))
        || personDocument.keySet().size() == 2 && personDocument.containsKey("ID_card") &&
                personDocument.containsKey("certificate_of_vaccination")){
            if(require_Passport){
                decisionData.add("Entry denied: missing required passport.");
            }
            if(require_AccessPermit){
                decisionData.add("Entry denied: missing required access permit.");
            }
            if(require_WorkPass){
                decisionData.add("Entry denied: missing required work pass.");
            }
            if(require_IdCard){
                if(checkCountry(personDocument, "Arstotzka")){
                    decisionData.add("Entry denied: missing required ID card.");
                }
            }
        }
    }

    private void diplomaticAccess(Map<String, String> personData){
        for(Map.Entry<String, String> personEntry : personData.entrySet()){
            String[] splitEntry = personEntry.getValue().split("\\n");
            for(String nation : splitEntry){
                if(nation.matches(".*ACCESS:.*") && !nation.matches(".*Arstotzka.*")){
                    decisionData.add("Entry denied: invalid diplomatic authorization.");
                }
            }
        }
    }

    public void setRequire_IdCard(boolean require_IdCard) {
        this.require_IdCard = require_IdCard;
    }

    public void setRequire_AccessPermit(boolean require_AccessPermit) {
        this.require_AccessPermit = require_AccessPermit;
    }

    public void setRequire_Passport(boolean require_Passport) {
        this.require_Passport = require_Passport;
    }

    public void setRequire_WorkPass(boolean require_WorkPass) {
        this.require_WorkPass = require_WorkPass;
    }

    public static Map<String, Set<String>> getNewBulletin() {
        return newBulletin;
    }

    public boolean getIdCard() {
        return require_IdCard;
    }

    public boolean getAccessPermit() {
        return require_AccessPermit;
    }

    public boolean getPassport() {
        return require_Passport;
    }

    public boolean getWorkPass() {
        return require_WorkPass;
    }
}
