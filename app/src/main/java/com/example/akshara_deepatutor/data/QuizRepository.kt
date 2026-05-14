package com.example.akshara_deepatutor.data

import com.example.akshara_deepatutor.data.model.QuizQuestion

object QuizRepository {
    private val allQuestions = listOf(
        // === SCIENCE ===
        // Chapter 1: Living Things (quizId: 1)
        QuizQuestion(101, "What is the basic unit of life?", listOf("Atom", "Cell", "Molecule", "Organ"), 1, 1),
        QuizQuestion(102, "Which organelle is the powerhouse of the cell?", listOf("Nucleus", "Ribosome", "Mitochondria", "Chloroplast"), 2, 1),
        QuizQuestion(103, "Plants make food using which process?", listOf("Respiration", "Digestion", "Photosynthesis", "Fermentation"), 2, 1),
        QuizQuestion(104, "Which of these is a unicellular organism?", listOf("Amoeba", "Human", "Tree", "Fish"), 0, 1),

        // Chapter 2: Matter (quizId: 2)
        QuizQuestion(201, "Which state of matter has a definite shape and volume?", listOf("Solid", "Liquid", "Gas", "Plasma"), 0, 2),
        QuizQuestion(202, "What is the boiling point of water at sea level?", listOf("90°C", "100°C", "110°C", "120°C"), 1, 2),
        QuizQuestion(203, "What is the process of a liquid turning into gas?", listOf("Freezing", "Melting", "Evaporation", "Condensation"), 2, 2),
        QuizQuestion(204, "Which of these is a chemical change?", listOf("Melting ice", "Burning wood", "Cutting paper", "Boiling water"), 1, 2),

        // Chapter 3: Energy (quizId: 3)
        QuizQuestion(301, "What is the primary source of energy for Earth?", listOf("The Moon", "The Sun", "Electricity", "Wind"), 1, 3),
        QuizQuestion(302, "Energy stored in a battery is ___ energy.", listOf("Kinetic", "Chemical", "Solar", "Thermal"), 1, 3),
        QuizQuestion(303, "The energy of motion is called?", listOf("Potential", "Static", "Kinetic", "Atomic"), 2, 3),
        QuizQuestion(304, "Which of these is a renewable source of energy?", listOf("Coal", "Natural Gas", "Solar Power", "Petroleum"), 2, 3),

        // Science Final Quiz Only (quizId: 1001)
        QuizQuestion(1001, "Who is known as the Father of Science?", listOf("Newton", "Galileo", "Einstein", "Aristotle"), 1, 1001),
        QuizQuestion(1002, "What gas do humans exhale?", listOf("Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"), 2, 1001),

        // === MATHS ===
        // Chapter 4: Algebra (quizId: 4)
        QuizQuestion(401, "If 2x = 10, what is x?", listOf("2", "5", "10", "20"), 1, 4),
        QuizQuestion(402, "Result of (a+b)^2?", listOf("a^2 + b^2", "a^2 + 2ab + b^2", "a^2 - b^2", "2a + 2b"), 1, 4),
        QuizQuestion(403, "Solve: 3x + 5 = 20", listOf("3", "5", "7", "10"), 1, 4),
        QuizQuestion(404, "What is the value of 5^0?", listOf("0", "1", "5", "10"), 1, 4),

        // Chapter 5: Geometry (quizId: 5)
        QuizQuestion(501, "How many sides does a hexagon have?", listOf("4", "5", "6", "8"), 2, 5),
        QuizQuestion(502, "An angle of 90 degrees is called?", listOf("Acute", "Obtuse", "Right", "Straight"), 2, 5),
        QuizQuestion(503, "Sum of angles in a triangle is?", listOf("90°", "180°", "270°", "360°"), 1, 5),
        QuizQuestion(504, "What is the longest side of a right triangle?", listOf("Base", "Height", "Hypotenuse", "None"), 2, 5),

        // Chapter 6: Trigonometry (quizId: 6)
        QuizQuestion(601, "What is sin(90°)?", listOf("0", "0.5", "1", "undefined"), 2, 6),
        QuizQuestion(602, "Value of tan(45°)?", listOf("0", "1", "sqrt(3)", "infinity"), 1, 6),
        QuizQuestion(603, "Ratio for cosine?", listOf("Opp/Adj", "Opp/Hyp", "Adj/Hyp", "Adj/Opp"), 2, 6),

        // Maths Final Quiz Only (quizId: 1002)
        QuizQuestion(1003, "What is the square root of 144?", listOf("10", "11", "12", "14"), 2, 1002),
        QuizQuestion(1004, "Who is known as the Prince of Mathematicians?", listOf("Gauss", "Euler", "Pythagoras", "Ramanujan"), 0, 1002),

        // === ENGLISH ===
        // Chapter 7: Grammar (quizId: 7)
        QuizQuestion(701, "Which is a noun?", listOf("Run", "Beautiful", "Apple", "Quickly"), 2, 7),
        QuizQuestion(702, "Which is a verb?", listOf("Dog", "Happy", "Eat", "Book"), 2, 7),
        QuizQuestion(703, "Identify the adjective: 'The tall man.'", listOf("The", "Tall", "Man", "None"), 1, 7),

        // Chapter 8: Vocabulary (quizId: 8)
        QuizQuestion(801, "Synonym of 'Happy'?", listOf("Sad", "Joyful", "Angry", "Brave"), 1, 8),
        QuizQuestion(802, "Antonym of 'Hot'?", listOf("Warm", "Cold", "Spicy", "Bright"), 1, 8),

        // English Final Quiz Only (quizId: 1003)
        QuizQuestion(1005, "Who wrote 'Romeo and Juliet'?", listOf("Charles Dickens", "William Shakespeare", "Mark Twain", "Leo Tolstoy"), 1, 1003),

        // === SOCIAL ===
        // Chapter 10: History (quizId: 10)
        QuizQuestion(1001, "Who was the first President of India?", listOf("Nehru", "Rajendra Prasad", "Ambedkar", "Gandhiji"), 1, 10),
        QuizQuestion(1002, "When did India get independence?", listOf("1942", "1947", "1950", "1952"), 1, 10),

        // Social Final Quiz Only (quizId: 1004)
        QuizQuestion(1006, "Which is the smallest continent?", listOf("Asia", "Africa", "Australia", "Europe"), 2, 1004),
        
        // Fallback
        QuizQuestion(9999, "What is 1 + 1?", listOf("1", "2", "3", "4"), 1, 100)
    )

    fun getQuestionsByChapter(chapterId: Int): List<QuizQuestion> {
        val questions = allQuestions.filter { it.chapterId == chapterId }
        return if (questions.isEmpty()) {
            allQuestions.filter { it.chapterId == 100 }
        } else {
            questions
        }
    }

    fun getQuestionsBySubject(chapterIds: List<Int>, subjectName: String): List<QuizQuestion> {
        val finalQuizId = when(subjectName) {
            "Science" -> 1001
            "Maths" -> 1002
            "English" -> 1003
            "Social" -> 1004
            else -> -1
        }
        
        // Combine chapter questions + final exclusive questions
        val pool = allQuestions.filter { it.chapterId in chapterIds || it.chapterId == finalQuizId }
        return pool.shuffled().take(10)
    }
}
