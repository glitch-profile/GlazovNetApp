package com.glazovnet.glazovnetapp.presentation.tariffs.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffModel
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffType
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.presentation.posts.list.PostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffsListScreen(
    onTariffClicked: (tariffId: String) -> Unit,
    onNavigationButtonPressed: () -> Unit,
    viewModel: TariffsListViewModel = hiltViewModel()
) {

    val tariffsState = viewModel.tariffsState.collectAsState()

    LaunchedEffect(null) {
        viewModel.loadTariffs()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.posts_list_screen_name))
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonPressed.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            },
            actions = {
                AnimatedVisibility(visible = !tariffsState.value.isLoading) {
                    IconButton(onClick = {
                        if (!tariffsState.value.isLoading) viewModel.loadTariffs()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Update page")
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (tariffsState.value.isLoading && tariffsState.value.data == null) {
                LoadingIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            } else if (tariffsState.value.stringResourceId != null) {
                RequestErrorScreen(
                    messageStringResource = tariffsState.value.stringResourceId,
                    additionalMessage = tariffsState.value.message
                )
            } else if (tariffsState.value.data != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    content = {
                        items(
                            items = tariffsState.value.data!!.toList(),
                            key = { it.first.toString() }
                        ) {
                                TariffsCard(
                                    tariffType = it.first,
                                    tariffs = it.second,
                                    onTariffClicked = onTariffClicked
                                )
//                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                )
            }
        }
    }

}

@Composable
private fun TariffsCard(
    tariffType: TariffType,
    tariffs: List<TariffModel>,
    onTariffClicked: (tariffId: String) -> Unit
) {
   Column(
       modifier = Modifier
           .fillMaxWidth(),
   ) {
       Row(
           modifier = Modifier
               .fillMaxWidth()
               .padding(horizontal = 16.dp),
           verticalAlignment = Alignment.CenterVertically
       ) {
           Text(
               text = stringResource(id = tariffType.stringResourceName),
               style = MaterialTheme.typography.headlineMedium,
               fontWeight = FontWeight.Bold,
               color = MaterialTheme.colorScheme.onSurface
           )
           Spacer(modifier = Modifier.width(8.dp))
           Text(
               text = tariffs.size.toString(),
               style = MaterialTheme.typography.titleMedium,
               fontWeight = FontWeight.Bold,
               color = MaterialTheme.colorScheme.onSurfaceVariant
           )
       }
       Column(
           modifier = Modifier
               .fillMaxWidth()
       ) {
           tariffs.forEach {tariff ->
               TariffCard(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 16.dp, vertical = 4.dp),
                   tariff = tariff,
                   onCardClicked = onTariffClicked
               )
           }
       }
   }
}