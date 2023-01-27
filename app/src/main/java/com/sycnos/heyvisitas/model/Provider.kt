package com.sycnos.heyvisitas.model

class Provider {
    companion object {
        fun random(): Model {
            val position = (0..10).random()
            return quotes[position]
        }
        private val quotes = listOf(
            Model(
                quote = "It’s not a bug. It’s an undocumented feature!",
                author = "Anonymous"
            ),
            Model(
                quote = "“Software Developer” – An organism that turns caffeine into software",
                author = "Anonymous"
            ),
            Model(
                quote = "If debugging is the process of removing software bugs, then programming must be the process of putting them in",
                author = "Edsger Dijkstra"
            ),
            Model(
                quote = "A user interface is like a joke. If you have to explain it, it’s not that good.",
                author = "Anonymous"
            ),
            Model(
                quote = "I don’t care if it works on your machine! We are not shipping your machine!",
                author = "Vidiu Platon"
            ),
            Model(
                quote = "Measuring programming progress by lines of code is like measuring aircraft building progress by weight.",
                author = "Bill Gates"
            ),
            Model(
                quote = "My code DOESN’T work, I have no idea why. My code WORKS, I have no idea why.",
                author = "Anonymous"
            ),
            Model(quote = "Things aren’t always #000000 and #FFFFFF", author = "Anonymous"),
            Model(quote = "Talk is cheap. Show me the code.", author = "Linus Torvalds"),
            Model(
                quote = "Software and cathedrals are much the same — first we build them, then we pray.",
                author = "Anonymous"
            ),
            Model(quote = "¿A que esperas?, suscríbete.", author = "AristiDevs")
        )
    }
}