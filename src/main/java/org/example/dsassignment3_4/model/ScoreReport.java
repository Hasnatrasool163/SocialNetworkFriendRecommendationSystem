package org.example.dsassignment3_4.model;


public class ScoreReport {

    private double score;
    private String report;
//    private Map<String, Object> scoreBreakdown;

    public ScoreReport(double score, String report) {
        this.score = score;
        this.report = report;
    }

    public double getScore() {
        return score;
    }

    public String getDetailedReport() {
        return report;
    }

    @Override
    public String toString() {
        return "Score: " + score + "\n" + report;
    }
}

//  Map<String, Object> scoreBreakdown = new HashMap<>();
//        scoreBreakdown.put("Mutual Friends", Map.of(
//                "Weight", mutualFriendWeight,
//                "Score", (double) mutualFriends / maxMutualFriends,
//                "Contribution", mutualFriendWeight * ((double) mutualFriends / maxMutualFriends)
//        ));
//        scoreBreakdown.put("Hobbies", Map.of(
//                "Weight", hobbiesWeight,
//                "Match", hobbies,
//                "Contribution", hobbiesWeight * (sharedHobbies ? 1 : 0)
//        ));
//        scoreBreakdown.put("Location", Map.of(
//                "Weight", locationWeight,
//                "Match", locationMatch,
//                "Contribution", locationWeight * (locationMatch ? 1 : 0)
//        ));
//        scoreBreakdown.put("Education", Map.of(
//                "Weight", educationWeight,
//                "Match", educationMatch,
//                "Contribution", educationWeight * (educationMatch ? 1 : 0)
//        ));
//        scoreBreakdown.put("Age Proximity", Map.of(
//                "Weight", ageProximityWeight,
//                "Match", ageProximity,
//                "Contribution", ageProximityWeight * (ageProximity ? 1 : 0)
//        ));
//        scoreBreakdown.put("Extras", Map.of(
//                "Weight", extrasWeight,
//                "Match", extraScore,
//                "Contribution", extrasWeight * extraScore
//        ));
//        scoreBreakdown.put("Mutual Likes", Map.of(
//                "Weight", postLikeWeight,
//                "Match", mutualLikes,
//                "Contribution", postLikeWeight * (mutualLikes ? 1 : 0)
//        ));
//        scoreBreakdown.put("Post Count", Map.of(
//                "Weight", postsCountWeight,
//                "Match", postScore,
//                "Contribution", postsCountWeight * postScore
//        ));

//  private void generateAndDisplayReport(ScoreReport scoreReport) {
//        ReportGenerator reportGenerator = new ReportGenerator();
//        try {
//                reportGenerator.generatePdfReport("ScoreReport.pdf", scoreReport.getScoreBreakdown(), scoreReport.getScore());
////                showAlert("PDF Saved", "The report has been saved");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }