package com.quizapp.model;

/**
 * Configurable grade scale with Turkish university grading (AA-FF).
 */
public class GradeScale {

    private int aaThreshold = 90;
    private int baThreshold = 85;
    private int bbThreshold = 80;
    private int cbThreshold = 75;
    private int ccThreshold = 70;
    private int dcThreshold = 65;
    private int ddThreshold = 60;
    private int fdThreshold = 50;
    private String passingGrade = "CC";

    public static final String STATUS_PASS = "PASS";
    public static final String STATUS_CONDITIONAL = "CONDITIONAL";
    public static final String STATUS_FAIL = "FAIL";

    public String getLetterGrade(double score) {
        if (score >= aaThreshold)
            return "AA";
        if (score >= baThreshold)
            return "BA";
        if (score >= bbThreshold)
            return "BB";
        if (score >= cbThreshold)
            return "CB";
        if (score >= ccThreshold)
            return "CC";
        if (score >= dcThreshold)
            return "DC";
        if (score >= ddThreshold)
            return "DD";
        if (score >= fdThreshold)
            return "FD";
        return "FF";
    }

    public String getStatus(double score) {
        return getStatusForGrade(getLetterGrade(score));
    }

    public String getStatusForGrade(String grade) {
        return switch (grade) {
            case "AA", "BA", "BB", "CB", "CC" -> STATUS_PASS;
            case "DC", "DD" -> STATUS_CONDITIONAL;
            default -> STATUS_FAIL;
        };
    }

    public boolean isPassing(double score) {
        return score >= getThreshold(passingGrade);
    }

    public int getThreshold(String grade) {
        return switch (grade.toUpperCase()) {
            case "AA" -> aaThreshold;
            case "BA" -> baThreshold;
            case "BB" -> bbThreshold;
            case "CB" -> cbThreshold;
            case "CC" -> ccThreshold;
            case "DC" -> dcThreshold;
            case "DD" -> ddThreshold;
            case "FD" -> fdThreshold;
            default -> 0;
        };
    }

    public void setThreshold(String grade, int threshold) {
        switch (grade.toUpperCase()) {
            case "AA" -> aaThreshold = threshold;
            case "BA" -> baThreshold = threshold;
            case "BB" -> bbThreshold = threshold;
            case "CB" -> cbThreshold = threshold;
            case "CC" -> ccThreshold = threshold;
            case "DC" -> dcThreshold = threshold;
            case "DD" -> ddThreshold = threshold;
            case "FD" -> fdThreshold = threshold;
        }
    }

    public String getPassingGrade() {
        return passingGrade;
    }

    public void setPassingGrade(String grade) {
        this.passingGrade = grade.toUpperCase();
    }

    public int getAaThreshold() {
        return aaThreshold;
    }

    public int getBaThreshold() {
        return baThreshold;
    }

    public int getBbThreshold() {
        return bbThreshold;
    }

    public int getCbThreshold() {
        return cbThreshold;
    }

    public int getCcThreshold() {
        return ccThreshold;
    }

    public int getDcThreshold() {
        return dcThreshold;
    }

    public int getDdThreshold() {
        return ddThreshold;
    }

    public int getFdThreshold() {
        return fdThreshold;
    }

    public static String[] getAllGrades() {
        return new String[] { "AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF" };
    }

    public static String[] getPassingGradeOptions() {
        return new String[] { "DD", "DC", "CC", "CB" };
    }
}
