package com.example.akshara_deepatutor.data.repository

import com.example.akshara_deepatutor.ui.screens.ContentSlide

object ChapterContentRepository {
    fun getContentForChapter(chapterName: String): List<ContentSlide> {
        return when (chapterName) {
            "Living Things" -> listOf(
                ContentSlide("Introduction", "What are Living Things?", "Living things are organisms that display key characteristics like growth, reproduction, and response to stimuli."),
                ContentSlide("Cells", "The Building Blocks", "All living things are made of cells. Some have only one (unicellular) while others have trillions (multicellular)."),
                ContentSlide("Characteristics", "How we identify life", "Living things need food, breathe (respiration), excrete waste, and move. They also grow over time.")
            )
            "Matter" -> listOf(
                ContentSlide("States of Matter", "Solid, Liquid, Gas", "Everything around us is matter. It exists in three main states: Solid (fixed shape), Liquid (takes shape of container), and Gas (fills space)."),
                ContentSlide("Changes in State", "Melting and Boiling", "Matter can change states when heated or cooled. For example, ice melts into water, and water boils into steam.")
            )
            "Algebra" -> listOf(
                ContentSlide("Variables", "Using Letters for Numbers", "Algebra uses letters like 'x' and 'y' to represent numbers that can change or are unknown."),
                ContentSlide("Equations", "Finding the Balance", "An equation like 2x = 10 tells us that 'x' must be 5 to make both sides equal.")
            )
            "History" -> listOf(
                ContentSlide("Independence", "The Struggle for Freedom", "India gained independence from British rule on August 15, 1947, after a long non-violent struggle."),
                ContentSlide("Leaders", "The Pillars of the Nation", "Great leaders like Mahatma Gandhi, Nehru, and Sardar Patel played pivotal roles in shaping modern India.")
            )
            else -> listOf(
                ContentSlide("Welcome", "Starting $chapterName", "Let's explore the core concepts of this chapter together."),
                ContentSlide("Learning Path", "Step-by-step Guide", "We will cover the theory, see some examples, and finally test your knowledge with a quiz.")
            )
        }
    }
}
