package com.vonage.android.shared.linkify

class LinkifyTest {

//    @ParameterizedTest
//    @MethodSource("inputs")
//    fun `should linkify the input`(input: String, links: List<LinkSpec>) {
//        assertEquals(links, Linkify.extract(input))
//    }
//
//    companion object {
//        @JvmStatic
//        fun inputs() = listOf(
//            of("hi there", emptyList<LinkSpec>()),
//            of(
//                "support@vonage.com", listOf(
//                    LinkSpec(
//                        tag = LinkTag.EMAIL,
//                        url = "mailto:support@vonage.com",
//                        start = 0,
//                        end = "support@vonage.com".length,
//                    )
//                )
//            ),
//            of(
//                "vonage.com", listOf(
//                    LinkSpec(
//                        tag = LinkTag.URL,
//                        url = "https://vonage.com",
//                        start = 0,
//                        end = "vonage.com".length,
//                    )
//                )
//            ),
//            of(
//                "🏁 vonage.com 🤘", listOf(
//                    LinkSpec(
//                        tag = LinkTag.URL,
//                        url = "https://vonage.com",
//                        start = 3,
//                        end = 13,
//                    )
//                )
//            ),
//            of(
//                "http://vonage.com", listOf(
//                    LinkSpec(
//                        tag = LinkTag.URL,
//                        url = "http://vonage.com",
//                        start = 0,
//                        end = "http://vonage.com".length,
//                    )
//                )
//            ),
//            of(
//                "https://www.vonage.com  support@vonage.com", listOf(
//                    LinkSpec(
//                        tag = LinkTag.URL,
//                        url = "https://www.vonage.com",
//                        start = 0,
//                        end = 22,
//                    ),
//                    LinkSpec(
//                        tag = LinkTag.EMAIL,
//                        url = "mailto:support@vonage.com",
//                        start = 24,
//                        end = 42,
//                    ),
//                )
//            ),
//            of(
//                "support@vonage.com https://www.vonage.com", listOf(
//                    LinkSpec(
//                        tag = LinkTag.EMAIL,
//                        url = "mailto:support@vonage.com",
//                        start = 0,
//                        end = 18,
//                    ),
//                    LinkSpec(
//                        tag = LinkTag.URL,
//                        url = "https://www.vonage.com",
//                        start = 19,
//                        end = 41,
//                    ),
//                )
//            ),
//        )
//    }
}
