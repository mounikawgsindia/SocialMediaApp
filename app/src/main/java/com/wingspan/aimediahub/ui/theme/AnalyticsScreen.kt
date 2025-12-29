package com.wingspan.aimediahub.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.R


@Composable
fun AnalyticsScreen(  bottomNavController: NavHostController,
                      rootNavController: NavHostController,
                      prefs: Prefs) {
    var facebookData= prefs.getFacebookPages()
    var twitterData= prefs.getTwitterAccount()
    var linkedInData=prefs.getLinkedInAccount()


    var networks= mutableListOf<ConnectedNetwork>()

    //facebook
    prefs.getFacebookPages().forEach { pages->
        networks.add(ConnectedNetwork(platform = Platform.FACEBOOK,  pageName = pages.name.orEmpty(),  platformIcon = R.drawable.ic_fb,  pageImage = pages.imageUrl.toString()))
    }
    //twitter
    prefs.getTwitterAccount()?.let{twitter ->
        networks.add(
            ConnectedNetwork(
                platform = Platform.TWITTER,
                pageName = twitter.name.orEmpty(),
                platformIcon = R.drawable.ic_twitter,
                pageImage = twitter.imageUrl.toString()
            )
        )
    }
// LinkedIn (single)
    prefs.getLinkedInAccount()?.let { linkedIn ->
        networks.add(
            ConnectedNetwork(
                platform = Platform.LINKEDIN,
                pageName = linkedIn.name.orEmpty(),
                platformIcon = R.drawable.ic_linkedin,
                pageImage = linkedIn.imageUrl.toString()
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Heading
        Text(
            text = "Analytics",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sub Heading
        Text(
            text = "Connected Networks",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Networks List
        LazyColumn {
            items(networks) { network ->
                ConnectedNetworkItem(
                    network = network,
                    onClick = {
                        rootNavController.navigate("")
                    }
                )
            }
        }
    }
}


@Composable
fun ConnectedNetworkItem(
    network: ConnectedNetwork,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Platform Logo
            Image(
                painter = painterResource(id = network.platformIcon),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Page Image
            AsyncImage(
                model = network.pageImage,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                placeholder = painterResource(R.drawable.back_icon),
                error = painterResource(R.drawable.back_icon)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Page Name
            Text(
                text = network.pageName,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            // Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
data class ConnectedNetwork(
    val platform: Platform,
    val pageName: String,
    val platformIcon: Int,
    val pageImage: String
)

enum class Platform {
    FACEBOOK, INSTAGRAM, TWITTER, LINKEDIN
}