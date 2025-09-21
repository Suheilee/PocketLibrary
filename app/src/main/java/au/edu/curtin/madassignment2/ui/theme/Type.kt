package au.edu.curtin.madassignment2.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import au.edu.curtin.madassignment2.R.font

val GoudyBookletter1911 = FontFamily(
    Font(font.goudy_bookletter_1911_regular, FontWeight.Normal)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = GoudyBookletter1911,
        fontWeight = FontWeight.Normal,
        fontSize = 50.sp,
        lineHeight = 35.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GoudyBookletter1911,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GoudyBookletter1911,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    )
)