package com.surrealdev.clispweb.content


import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.surrealdev.clispweb.style.*


@Composable
fun PageFooter() {
    Footer({
        style {
            flexShrink(0)
            property("box-sizing", value("border-box"))
        }
    }) {
        Section({
            classes(WtSections.wtSectionBgGrayDark)
            style {
                property("padding", value("24px 0"))
            }
        }) {
            Div({ classes(WtContainer.wtContainer) }) {

                CopyrightInFooter()
            }
        }
    }
}

@Composable
private fun CopyrightInFooter() {
    Div({
        classes(WtRows.wtRow, WtRows.wtRowSizeM, WtRows.wtRowSmAlignItemsCenter, WtOffsets.wtTopOffset48)
        style {
            justifyContent(JustifyContent.SpaceEvenly)
            flexWrap(FlexWrap.Wrap)
            property("padding", value("0px 12px"))
        }
    }) {
        Span({
            classes(WtTexts.wtText3, WtTexts.wtTextPale)
        }) {
            Text("Copyright © 2016-2021  Surreal Development LLC")
        }

        Span({
            classes(WtTexts.wtText3, WtTexts.wtTextPale)
        }) {
            Text("Developed with Jetpack Compose for Web")
        }
    }
}

@Composable
private fun SocialIconLink(link: SocialLink) {
    A(attrs = {
        classes(WtTexts.wtSocialButtonItem)
        target(ATarget.Blank)
    }, href = link.url) {
        Img(src = link.iconSvg) {}
    }
}

private data class SocialLink(
    val id: String,
    val url: String,
    val title: String,
    val iconSvg: String
)

private fun getSocialLinks(): List<SocialLink> {
    return listOf(
        SocialLink("facebook", "https://www.facebook.com/JetBrains", "JetBrains on Facebook", "ic_fb.svg"),
        SocialLink("twitter", "https://twitter.com/jetbrains", "JetBrains on Twitter", "ic_twitter.svg"),
        SocialLink(
            "linkedin",
            "https://www.linkedin.com/company/jetbrains",
            "JetBrains on Linkedin",
            "ic_linkedin.svg"
        ),
        SocialLink("youtube", "https://www.youtube.com/user/JetBrainsTV", "JetBrains on YouTube", "ic_youtube.svg"),
        SocialLink("instagram", "https://www.instagram.com/jetbrains/", "JetBrains on Instagram", "ic_insta.svg"),
        SocialLink("blog", "https://blog.jetbrains.com/", "JetBrains blog", "ic_jb_blog.svg"),
        SocialLink("rss", "https://blog.jetbrains.com/feed/", "JetBrains RSS Feed", "ic_feed.svg"),
    )
}