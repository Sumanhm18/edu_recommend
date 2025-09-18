package com.education.education.service;

import com.education.education.entity.Quiz;
import com.education.education.repository.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QuizDataInitializer implements CommandLineRunner {

    @Autowired
    private QuizRepository quizRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no quizzes exist
        if (quizRepository.count() == 0) {
            initializeAptitudeQuiz();
            initializeInterestQuiz();
            System.out.println("Sample quiz data initialized successfully!");
        }
    }

    /**
     * Create Aptitude Assessment Quiz
     */
    private void initializeAptitudeQuiz() throws Exception {
        List<Map<String, Object>> questions = new ArrayList<>();

        // Mathematical Questions
        questions.add(
                createQuestion(1, "mathematical", "If a train travels at 60 km/h for 2 hours, how far does it travel?",
                        new String[] { "120 km", "100 km", "140 km", "80 km" }, "A", 2));

        questions.add(createQuestion(2, "mathematical", "What is 15% of 200?",
                new String[] { "30", "25", "35", "20" }, "A", 2));

        questions.add(createQuestion(3, "mathematical", "If x + 5 = 12, what is x?",
                new String[] { "7", "6", "8", "5" }, "A", 2));

        // Verbal Questions
        questions.add(createQuestion(4, "verbal", "Choose the word closest in meaning to 'ABUNDANT':",
                new String[] { "Scarce", "Plentiful", "Limited", "Rare" }, "B", 2));

        questions.add(createQuestion(5, "verbal", "Complete the analogy: Book is to Library as _____ is to Museum",
                new String[] { "Art", "Building", "People", "Ticket" }, "A", 2));

        questions.add(createQuestion(6, "verbal", "Which sentence is grammatically correct?",
                new String[] { "He don't like it", "He doesn't likes it", "He doesn't like it", "He not like it" }, "C",
                2));

        // Analytical Questions
        questions.add(createQuestion(7, "analytical", "What comes next in the series: 2, 6, 18, 54, ?",
                new String[] { "162", "108", "216", "150" }, "A", 3));

        questions.add(createQuestion(8, "analytical",
                "If all roses are flowers and some flowers are red, which statement is definitely true?",
                new String[] { "All roses are red", "Some roses are red", "All flowers are roses",
                        "Some roses may be red" },
                "D", 3));

        questions.add(createQuestion(9, "analytical", "A cube has how many faces?",
                new String[] { "4", "6", "8", "12" }, "B", 2));

        // Technical/Science Questions
        questions.add(createQuestion(10, "technical", "What is the chemical symbol for water?",
                new String[] { "H2O", "CO2", "NaCl", "O2" }, "A", 2));

        questions.add(createQuestion(11, "technical", "Which force keeps planets in orbit around the sun?",
                new String[] { "Magnetic force", "Gravitational force", "Electric force", "Nuclear force" }, "B", 2));

        questions.add(createQuestion(12, "technical", "In a computer, what does CPU stand for?",
                new String[] { "Central Processing Unit", "Computer Personal Unit", "Central Program Unit",
                        "Control Processing Unit" },
                "A", 2));

        String questionsJson = objectMapper.writeValueAsString(questions);

        Quiz aptitudeQuiz = new Quiz(
                "General Aptitude Assessment",
                "This quiz assesses your mathematical, verbal, analytical, and technical aptitude to recommend suitable academic streams.",
                questionsJson,
                "12th");

        quizRepository.save(aptitudeQuiz);
    }

    /**
     * Create Interest Assessment Quiz
     */
    private void initializeInterestQuiz() throws Exception {
        List<Map<String, Object>> questions = new ArrayList<>();

        // Interest-based questions
        questions.add(createQuestion(1, "analytical", "Which activity interests you most?",
                new String[] { "Solving math problems", "Reading novels", "Conducting experiments",
                        "Managing a business" },
                "C", 3));

        questions.add(createQuestion(2, "verbal", "What type of subjects do you enjoy?",
                new String[] { "Languages and literature", "Mathematics and physics", "History and social studies",
                        "Economics and business" },
                "A", 3));

        questions.add(createQuestion(3, "technical", "What career sounds most appealing?",
                new String[] { "Doctor/Engineer", "Teacher/Writer", "Lawyer/Social worker", "Businessman/Accountant" },
                "A", 3));

        questions.add(createQuestion(4, "analytical", "How do you prefer to solve problems?",
                new String[] { "Using logical reasoning", "Through research and reading", "By experimenting",
                        "By discussing with others" },
                "A", 3));

        questions.add(createQuestion(5, "mathematical", "Which statement describes you best?",
                new String[] { "I love working with numbers", "I enjoy creative writing", "I like helping people",
                        "I'm interested in technology" },
                "A", 3));

        questions.add(createQuestion(6, "verbal", "What type of learning environment do you prefer?",
                new String[] { "Laboratory experiments", "Library research", "Group discussions",
                        "Practical workshops" },
                "B", 2));

        questions.add(createQuestion(7, "technical", "Which subject combination interests you most?",
                new String[] { "Physics, Chemistry, Math", "English, History, Geography", "Math, Economics, Accounts",
                        "Biology, Chemistry, Physics" },
                "A", 3));

        questions.add(createQuestion(8, "analytical", "What motivates you most in studies?",
                new String[] { "Understanding how things work", "Expressing ideas creatively",
                        "Solving real-world problems", "Building successful ventures" },
                "A", 3));

        String questionsJson = objectMapper.writeValueAsString(questions);

        Quiz interestQuiz = new Quiz(
                "Career Interest Assessment",
                "This quiz helps identify your interests and preferences to suggest the most suitable academic stream and career path.",
                questionsJson,
                "12th");

        quizRepository.save(interestQuiz);
    }

    /**
     * Helper method to create question objects
     */
    private Map<String, Object> createQuestion(int id, String category, String question,
            String[] options, String correctAnswer, int points) {
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("id", id);
        questionMap.put("category", category);
        questionMap.put("question", question);
        questionMap.put("options", Map.of(
                "A", options[0],
                "B", options[1],
                "C", options[2],
                "D", options[3]));
        questionMap.put("correctAnswer", correctAnswer);
        questionMap.put("points", points);
        return questionMap;
    }
}