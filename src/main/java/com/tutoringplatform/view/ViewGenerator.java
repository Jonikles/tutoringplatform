package com.tutoringplatform.view;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.tutoringplatform.models.Booking;
import com.tutoringplatform.models.Review;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;

public class ViewGenerator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static void generateLoginView() throws IOException {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("    <title>Tutoring Platform - Login</title>\n");
        htmlBuilder.append("    <style>\n");
        htmlBuilder.append("        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }\n");
        htmlBuilder.append("        .container { max-width: 400px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
        htmlBuilder.append("        input { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 4px; }\n");
        htmlBuilder.append("        button { width: 100%; padding: 12px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }\n");
        htmlBuilder.append("        button:hover { background: #0056b3; }\n");
        htmlBuilder.append("        .toggle { text-align: center; margin-top: 20px; }\n");
        htmlBuilder.append("        a { color: #007bff; text-decoration: none; }\n");
        htmlBuilder.append("    </style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");
        htmlBuilder.append("    <div class=\"container\">\n");
        htmlBuilder.append("        <h2>Login to Tutoring Platform</h2>\n");
        htmlBuilder.append("        <form>\n");
        htmlBuilder.append("            <input type=\"email\" placeholder=\"Email\" required>\n");
        htmlBuilder.append("            <input type=\"password\" placeholder=\"Password\" required>\n");
        htmlBuilder.append("            <button type=\"submit\">Login</button>\n");
        htmlBuilder.append("        </form>\n");
        htmlBuilder.append("        <div class=\"toggle\">\n");
        htmlBuilder.append("            <p>Don't have an account? <a href=\"signup.html\">Sign up</a></p>\n");
        htmlBuilder.append("        </div>\n");
        htmlBuilder.append("    </div>\n");
        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>");

        String html = htmlBuilder.toString();
        writeToFile("login.html", html);
    }

    public static void generateStudentDashboard(Student student, List<Tutor> tutors,
            List<Booking> bookings) throws IOException {
        StringBuilder tutorList = new StringBuilder();
        for (Tutor tutor : tutors) {
            tutorList.append(String.format(
                    "<div class=\"tutor-card\">" +
                    "<h3>%s</h3>" +
                    "<p>%s</p>" +
                    "<p>Rate: $%.2f/hour</p>" +
                    "<p>Rating: %.1f/5</p>" +
                    "<p>Subjects: %s</p>" +
                    "<button onclick=\"bookTutor('%s')\">Book Session</button>" +
                    "</div>",
                    tutor.getName(),
                    tutor.getDescription(),
                    tutor.getHourlyRate(),
                    tutor.getAverageRating(),
                    tutor.getSubjects().stream().map(Subject::getName).reduce((a, b) -> a + ", " + b).orElse("None"),
                    tutor.getId()));
        }

        StringBuilder bookingList = new StringBuilder();
        for (Booking booking : bookings) {
            bookingList.append(String.format(
                    "<div class=\"booking-card\">" +
                    "<h4>%s - %s</h4>" +
                    "<p>Date: %s</p>" +
                    "<p>Duration: %d hours</p>" +
                    "<p>Cost: $%.2f</p>" +
                    "<p>Status: %s</p>" +
                    "</div>",
                    booking.getSubject().getName(),
                    booking.getStatus(), 
                    booking.getDateTime().format(formatter),
                    booking.getDurationHours(),
                    booking.getTotalCost(),
                    booking.getStatus() 
            ));
        }
        String html = String.format(
                "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <title>Student Dashboard</title>"
                + "    <style>"
                + "        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }"
                + "        .header { background: #007bff; color: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; }"
                + "        .container { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; }"
                + "        .section { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }"
                + "        .tutor-card, .booking-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 4px; }"
                + "        .search-filters { display: flex; gap: 10px; margin-bottom: 20px; flex-wrap: wrap; }"
                + "        input, select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }"
                + "        button { padding: 10px 20px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }"
                + "        button:hover { background: #0056b3; }"
                + "        .balance { font-size: 24px; font-weight: bold; color: #28a745; }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class=\"header\">"
                + "        <h1>Welcome, %s!</h1>"
                + "        <p>Balance: <span class=\"balance\">$%.2f</span></p>"
                + "        <button onclick=\"addFunds()\">Add Funds</button>"
                + "    </div>"
                + "    <div class=\"container\">"
                + "        <div class=\"section\">"
                + "            <h2>Find Tutors</h2>"
                + "            <div class=\"search-filters\">"
                + "                <select id=\"subject\">"
                + "                    <option value=\"\">All Subjects</option>"
                + "                    <option value=\"Math\">Mathematics</option>"
                + "                    <option value=\"Science\">Science</option>"
                + "                    <option value=\"Language\">Language</option>"
                + "                    <option value=\"Programming\">Programming</option>"
                + "                </select>"
                + "                <input type=\"number\" placeholder=\"Min Price\" id=\"minPrice\">"
                + "                <input type=\"number\" placeholder=\"Max Price\" id=\"maxPrice\">"
                + "                <input type=\"number\" placeholder=\"Min Rating\" id=\"minRating\" min=\"0\" max=\"5\" step=\"0.5\">"
                + "                <button onclick=\"searchTutors()\">Search</button>"
                + "            </div>"
                + "            <div id=\"tutorList\">"
                + "                %s"
                + "            </div>"
                + "        </div>"
                + "        <div class=\"section\">"
                + "            <h2>My Bookings</h2>"
                + "            <div id=\"bookingList\">"
                + "                %s"
                + "            </div>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>",
                student.getName(), student.getBalance(), tutorList.toString(), bookingList.toString());

        writeToFile("student_dashboard.html", html);
    }

    public static void generateTutorDashboard(Tutor tutor, List<Booking> bookings) throws IOException {
        StringBuilder availabilityTable = new StringBuilder();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

        availabilityTable.append("<table><tr><th>Day</th><th>Available Hours</th></tr>");
        for (String day : days) {
            List<Integer> hours = tutor.getAvailability().get(day);
            String hoursStr = hours.stream()
                    .map(h -> h + ":00") // Assuming h is an integer hour
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None");
            availabilityTable.append(String.format("<tr><td>%s</td><td>%s</td></tr>", day, hoursStr));
        }
        availabilityTable.append("</table>");
        String html = String.format(
                "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <title>Tutor Dashboard</title>"
                + "    <style>"
                + "        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }"
                + "        .header { background: #28a745; color: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; }"
                + "        .stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 20px; }"
                + "        .stat-card { background: white; padding: 20px; border-radius: 8px; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }"
                + "        .section { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }"
                + "        table { width: 100%%; border-collapse: collapse; }"
                + "        th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }"
                + "        .earnings { font-size: 32px; font-weight: bold; color: #28a745; }"
                + "        .rating { font-size: 24px; color: #ffc107; }"
                + "        button { padding: 10px 20px; background: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class=\"header\">"
                + "        <h1>Tutor Dashboard - %s</h1>"
                + "        <p>%s</p>"
                + "    </div>"
                + "    <div class=\"stats\">"
                + "        <div class=\"stat-card\">"
                + "            <h3>Total Earnings</h3>"
                + "            <p class=\"earnings\">$%.2f</p>"
                + "        </div>"
                + "        <div class=\"stat-card\">"
                + "            <h3>Average Rating</h3>"
                + "            <p class=\"rating\">%.1f/5</p>"
                + "        </div>"
                + "        <div class=\"stat-card\">"
                + "            <h3>Hourly Rate</h3>"
                + "            <p class=\"earnings\">$%.2f</p>"
                + "        </div>"
                + "    </div>"
                + "    <div class=\"section\">"
                + "        <h2>My Availability</h2>"
                + "        %s"
                + "        <button onclick=\"editAvailability()\">Edit Availability</button>"
                + "    </div>"
                + "    <div class=\"section\">"
                + "        <h2>Subjects</h2>"
                + "        <p>%s</p>"
                + "        <button onclick=\"editSubjects()\">Edit Subjects</button>"
                + "    </div>"
                + "    <div class=\"section\">"
                + "        <h2>Recent Reviews</h2>"
                + "        <div id=\"reviewsList\">"
                + "            %s"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>",
                tutor.getName(),
                tutor.getDescription(),
                tutor.getEarnings(),
                tutor.getAverageRating(),
                tutor.getHourlyRate(),
                availabilityTable.toString(),
                tutor.getSubjects().stream().map(Subject::getName).reduce((a, b) -> a + ", " + b).orElse("None"),
                generateReviewsHtml(tutor.getReviewsReceived()));

        writeToFile("tutor_dashboard.html", html);
    }

    private static String generateReviewsHtml(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "<p>No reviews yet.</p>";
        }

        StringBuilder html = new StringBuilder();
        for (Review review : reviews) {
            html.append(String.format(
                    "<div style=\"border: 1px solid #ddd; padding: 10px; margin: 10px 0; border-radius: 4px;\">" +
                    "<p><strong>Rating:</strong> %d/5</p>" +
                    "<p>%s</p>" +
                    "<p><small>%s</small></p>" +
                    "</div>",
                    review.getRating(), review.getComment(), review.getTimestamp().format(formatter)));
        }
        return html.toString();
    }

    private static void writeToFile(String filename, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            System.out.println("Generated view: " + filename);
        }
    }
}