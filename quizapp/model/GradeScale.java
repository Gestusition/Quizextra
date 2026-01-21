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
        return new String[] { "DD", "DC", "CC", "CB", "BB", "BA", "AA" };
    }

    /**
     * Validates if a new threshold value is valid for the given grade.
     * Ensures proper ordering: AA > BA > BB > CB > CC > DC > DD > FD > 0
     * 
     * @param grade     The grade to set threshold for
     * @param threshold The proposed threshold value
     * @return null if valid, error message if invalid
     */
    public String validateThreshold(String grade, int threshold) {
        if (threshold < 1 || threshold > 100) {
            return "Threshold must be between 1 and 100.";
        }

        // Check ordering constraints based on grade
        return switch (grade.toUpperCase()) {
            case "AA" -> {
                // AA must be > BA
                if (threshold <= baThreshold) {
                    yield "AA threshold must be greater than BA (" + baThreshold + ").";
                }
                yield null;
            }
            case "BA" -> {
                // BA must be < AA and > BB
                if (threshold >= aaThreshold) {
                    yield "BA threshold must be less than AA (" + aaThreshold + ").";
                }
                if (threshold <= bbThreshold) {
                    yield "BA threshold must be greater than BB (" + bbThreshold + ").";
                }
                yield null;
            }
            case "BB" -> {
                if (threshold >= baThreshold) {
                    yield "BB threshold must be less than BA (" + baThreshold + ").";
                }
                if (threshold <= cbThreshold) {
                    yield "BB threshold must be greater than CB (" + cbThreshold + ").";
                }
                yield null;
            }
            case "CB" -> {
                if (threshold >= bbThreshold) {
                    yield "CB threshold must be less than BB (" + bbThreshold + ").";
                }
                if (threshold <= ccThreshold) {
                    yield "CB threshold must be greater than CC (" + ccThreshold + ").";
                }
                yield null;
            }
            case "CC" -> {
                if (threshold >= cbThreshold) {
                    yield "CC threshold must be less than CB (" + cbThreshold + ").";
                }
                if (threshold <= dcThreshold) {
                    yield "CC threshold must be greater than DC (" + dcThreshold + ").";
                }
                yield null;
            }
            case "DC" -> {
                if (threshold >= ccThreshold) {
                    yield "DC threshold must be less than CC (" + ccThreshold + ").";
                }
                if (threshold <= ddThreshold) {
                    yield "DC threshold must be greater than DD (" + ddThreshold + ").";
                }
                yield null;
            }
            case "DD" -> {
                if (threshold >= dcThreshold) {
                    yield "DD threshold must be less than DC (" + dcThreshold + ").";
                }
                if (threshold <= fdThreshold) {
                    yield "DD threshold must be greater than FD (" + fdThreshold + ").";
                }
                yield null;
            }
            case "FD" -> {
                if (threshold >= ddThreshold) {
                    yield "FD threshold must be less than DD (" + ddThreshold + ").";
                }
                if (threshold < 1) {
                    yield "FD threshold must be at least 1.";
                }
                yield null;
            }
            default -> "Invalid grade: " + grade;
        };
    }
}
