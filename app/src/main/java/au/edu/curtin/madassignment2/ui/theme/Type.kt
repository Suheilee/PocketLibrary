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
        fontSize = 56.sp,
        lineHeight = 36.sp,
        letterSpacing = 1.sp
    )
)