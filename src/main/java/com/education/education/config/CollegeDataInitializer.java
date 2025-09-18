package com.education.education.config;

import com.education.education.entity.College;
import com.education.education.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3) // Run after DataInitializer (Order 2)
public class CollegeDataInitializer implements CommandLineRunner {

    @Autowired
    private CollegeRepository collegeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if colleges already exist
        if (collegeRepository.count() > 0) {
            System.out.println("College data already exists. Skipping initialization.");
            return;
        }

        System.out.println("Initializing sample college data...");

        // Create sample colleges
        List<College> colleges = List.of(
                // Karnataka - Bangalore Urban
                createCollege("Indian Institute of Science (IISc)", "Bangalore Urban", "Karnataka",
                        "560012", "CV Raman Road, Bangalore", "Deemed_University", "Premier",
                        "Science,Engineering", 1909, "https://iisc.ac.in", "080-22932424",
                        "registrar@iisc.ac.in", "₹2,00,000 - ₹5,00,000", 95.5),

                createCollege("Indian Institute of Management Bangalore", "Bangalore Urban", "Karnataka",
                        "560076", "Bannerghatta Road, Bangalore", "Government", "Premier",
                        "Commerce,Management", 1973, "https://iimb.ac.in", "080-26993000",
                        "admissions@iimb.ac.in", "₹20,00,000 - ₹25,00,000", 98.2),

                createCollege("University Visvesvaraya College of Engineering", "Bangalore Urban", "Karnataka",
                        "560001", "K.R. Circle, Bangalore", "Government", "Excellent",
                        "Science,Engineering", 1917, "https://uvce.ac.in", "080-22961856",
                        "principal@uvce.ac.in", "₹50,000 - ₹1,00,000", 85.3),

                createCollege("St. Joseph's College", "Bangalore Urban", "Karnataka",
                        "560025", "36 Lalbagh Road, Bangalore", "Private", "Excellent",
                        "Science,Commerce,Arts", 1882, "https://sjc.ac.in", "080-22214756",
                        "principal@sjc.ac.in", "₹1,00,000 - ₹2,00,000", 78.9),

                createCollege("Government First Grade College", "Bangalore Urban", "Karnataka",
                        "560053", "Malleshwaram, Bangalore", "Government", "Good",
                        "Science,Commerce,Arts", 1961, "", "080-23344567",
                        "principal.gfgc@gmail.com", "₹10,000 - ₹25,000", 65.4),

                // Karnataka - Mysore
                createCollege("University of Mysore", "Mysore", "Karnataka",
                        "570005", "Crawford Hall, Mysore", "Government", "Excellent",
                        "Science,Commerce,Arts", 1916, "https://uni-mysore.ac.in", "0821-2419669",
                        "registrar@uni-mysore.ac.in", "₹15,000 - ₹50,000", 82.1),

                createCollege("JSS College of Arts, Commerce and Science", "Mysore", "Karnataka",
                        "570025", "JSS Nagar, Mysore", "Private", "Good",
                        "Science,Commerce,Arts", 1954, "https://jsscacs.edu.in", "0821-2548400",
                        "principal@jsscacs.edu.in", "₹80,000 - ₹1,50,000", 71.8),

                // Maharashtra - Mumbai
                createCollege("Indian Institute of Technology Bombay", "Mumbai", "Maharashtra",
                        "400076", "Powai, Mumbai", "Government", "Premier",
                        "Science,Engineering", 1958, "https://iitb.ac.in", "022-25722545",
                        "dean.ap@iitb.ac.in", "₹2,00,000 - ₹4,00,000", 97.8),

                createCollege("St. Xavier's College", "Mumbai", "Maharashtra",
                        "400001", "5 Mahapalika Marg, Mumbai", "Private", "Excellent",
                        "Science,Commerce,Arts", 1869, "https://xaviers.edu", "022-22620661",
                        "principal@xaviers.edu", "₹50,000 - ₹1,20,000", 88.5),

                createCollege("Government Polytechnic Mumbai", "Mumbai", "Maharashtra",
                        "400031", "Bandra, Mumbai", "Government", "Good",
                        "Science,Engineering", 1946, "", "022-26420789",
                        "gpm@gmail.com", "₹15,000 - ₹30,000", 69.3),

                // Maharashtra - Pune
                createCollege("College of Engineering Pune", "Pune", "Maharashtra",
                        "411005", "Wellesley Road, Pune", "Government", "Excellent",
                        "Science,Engineering", 1854, "https://coep.org.in", "020-25507001",
                        "principal@coep.ac.in", "₹1,00,000 - ₹2,00,000", 89.7),

                createCollege("Fergusson College", "Pune", "Maharashtra",
                        "411004", "FC Road, Pune", "Government", "Excellent",
                        "Science,Commerce,Arts", 1885, "https://fergusson.edu", "020-25519771",
                        "principal@fergusson.edu", "₹25,000 - ₹60,000", 85.2),

                // Tamil Nadu - Chennai
                createCollege("Indian Institute of Technology Madras", "Chennai", "Tamil Nadu",
                        "600036", "Sardar Patel Road, Chennai", "Government", "Premier",
                        "Science,Engineering", 1959, "https://iitm.ac.in", "044-22578000",
                        "dean@iitm.ac.in", "₹2,00,000 - ₹4,00,000", 96.8),

                createCollege("Loyola College", "Chennai", "Tamil Nadu",
                        "600034", "Sterling Road, Chennai", "Private", "Excellent",
                        "Science,Commerce,Arts", 1925, "https://loyolacollege.edu", "044-28178200",
                        "principal@loyolacollege.edu", "₹60,000 - ₹1,40,000", 84.6),

                createCollege("Government Arts College", "Chennai", "Tamil Nadu",
                        "600005", "Nandanam, Chennai", "Government", "Good",
                        "Arts,Commerce", 1967, "", "044-24330765",
                        "gac.nandanam@gmail.com", "₹8,000 - ₹20,000", 72.4),

                // West Bengal - Kolkata
                createCollege("Presidency University", "Kolkata", "West Bengal",
                        "700073", "86/1 College Street, Kolkata", "Government", "Premier",
                        "Science,Commerce,Arts", 1817, "https://presiuniv.ac.in", "033-22419248",
                        "registrar@presiuniv.ac.in", "₹20,000 - ₹50,000", 91.3),

                createCollege("St. Xavier's College Kolkata", "Kolkata", "West Bengal",
                        "700016", "30 Mother Teresa Sarani, Kolkata", "Private", "Excellent",
                        "Science,Commerce,Arts", 1860, "https://sxccal.edu", "033-22870429",
                        "principal@sxccal.edu", "₹40,000 - ₹90,000", 87.9),

                // Delhi
                createCollege("Delhi University - St. Stephen's College", "New Delhi", "Delhi",
                        "110007", "University Enclave, Delhi", "Government", "Premier",
                        "Science,Commerce,Arts", 1881, "https://ststephens.edu", "011-27667271",
                        "principal@ststephens.edu", "₹30,000 - ₹70,000", 93.2),

                createCollege("Lady Shri Ram College for Women", "New Delhi", "Delhi",
                        "110024", "Lajpat Nagar IV, Delhi", "Government", "Excellent",
                        "Commerce,Arts", 1956, "https://lsr.edu.in", "011-29214580",
                        "principal@lsr.edu.in", "₹25,000 - ₹55,000", 89.1),

                // Rajasthan - Jaipur
                createCollege("Indian Institute of Technology Jodhpur", "Jodhpur", "Rajasthan",
                        "342037", "NH 65, Nagaur Road, Jodhpur", "Government", "Premier",
                        "Science,Engineering", 2008, "https://iitj.ac.in", "0291-2801154",
                        "admin@iitj.ac.in", "₹2,00,000 - ₹4,00,000", 94.7),

                createCollege("Government College Ajmer", "Ajmer", "Rajasthan",
                        "305001", "Nasirabad Road, Ajmer", "Government", "Good",
                        "Science,Commerce,Arts", 1956, "", "0145-2627819",
                        "principal.gcajmer@gmail.com", "₹12,000 - ₹28,000", 68.9),

                // Gujarat - Ahmedabad
                createCollege("Indian Institute of Management Ahmedabad", "Ahmedabad", "Gujarat",
                        "380015", "Vastrapur, Ahmedabad", "Government", "Premier",
                        "Commerce,Management", 1961, "https://iima.ac.in", "079-66324658",
                        "pgpadm@iima.ac.in", "₹20,00,000 - ₹25,00,000", 98.5),

                createCollege("Gujarat University", "Ahmedabad", "Gujarat",
                        "380009", "University Road, Ahmedabad", "Government", "Excellent",
                        "Science,Commerce,Arts", 1949, "https://gujaratuniversity.ac.in", "079-26300488",
                        "registrar@gujaratuniversity.ac.in", "₹15,000 - ₹40,000", 79.6));

        // Save all colleges to database
        collegeRepository.saveAll(colleges);

        System.out.println("Successfully initialized " + colleges.size() + " sample colleges!");
    }

    private College createCollege(String name, String district, String state, String pincode,
            String address, String type, String tier, String streams,
            Integer year, String website, String phone, String email,
            String feesRange, Double placementRate) {
        College college = new College();
        college.setName(name);
        college.setDistrict(district);
        college.setState(state);
        college.setPincode(pincode);
        college.setAddress(address);
        college.setCollegeType(type);
        college.setCollegeTier(tier);
        college.setStreamsOffered(streams);
        college.setEstablishmentYear(year);
        college.setWebsite(website);
        college.setPhone(phone);
        college.setEmail(email);
        college.setFeesRange(feesRange);
        college.setPlacementRate(placementRate);
        college.setIsActive(true);

        // Add some sample course and facility data
        college.setCoursesOffered(generateCoursesJson(streams));
        college.setFacilitiesJson(generateFacilitiesJson(tier));
        college.setContactInfo(generateContactInfo(phone, email, website));

        return college;
    }

    private String generateCoursesJson(String streams) {
        StringBuilder courses = new StringBuilder("[");

        if (streams.contains("Science")) {
            courses.append("{\"name\":\"B.Sc Physics\",\"duration\":\"3 years\",\"seats\":60},");
            courses.append("{\"name\":\"B.Sc Chemistry\",\"duration\":\"3 years\",\"seats\":60},");
            courses.append("{\"name\":\"B.Sc Mathematics\",\"duration\":\"3 years\",\"seats\":40},");
        }

        if (streams.contains("Commerce")) {
            courses.append("{\"name\":\"B.Com\",\"duration\":\"3 years\",\"seats\":120},");
            courses.append("{\"name\":\"BBA\",\"duration\":\"3 years\",\"seats\":60},");
        }

        if (streams.contains("Arts")) {
            courses.append("{\"name\":\"BA English\",\"duration\":\"3 years\",\"seats\":80},");
            courses.append("{\"name\":\"BA History\",\"duration\":\"3 years\",\"seats\":60},");
        }

        if (streams.contains("Engineering")) {
            courses.append("{\"name\":\"B.Tech CSE\",\"duration\":\"4 years\",\"seats\":120},");
            courses.append("{\"name\":\"B.Tech ECE\",\"duration\":\"4 years\",\"seats\":60},");
        }

        // Remove trailing comma and close array
        String result = courses.toString();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        result += "]";

        return result;
    }

    private String generateFacilitiesJson(String tier) {
        StringBuilder facilities = new StringBuilder("[");

        // Basic facilities for all colleges
        facilities.append("\"Library\",\"Computer Lab\",\"WiFi\",\"Canteen\"");

        if ("Premier".equals(tier)) {
            facilities.append(
                    ",\"Research Centers\",\"Auditorium\",\"Sports Complex\",\"Hostel\",\"Industry Partnerships\"");
        } else if ("Excellent".equals(tier)) {
            facilities.append(",\"Auditorium\",\"Sports Facilities\",\"Hostel\",\"Placement Cell\"");
        } else if ("Good".equals(tier)) {
            facilities.append(",\"Sports Ground\",\"Placement Cell\"");
        }

        facilities.append("]");
        return facilities.toString();
    }

    private String generateContactInfo(String phone, String email, String website) {
        return String.format("{\"phone\":\"%s\",\"email\":\"%s\",\"website\":\"%s\"}",
                phone, email, website);
    }
}