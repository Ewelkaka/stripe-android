package com.stripe.android.financialconnections.screenshottests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.airbnb.android.showkase.models.Showkase
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.NightMode
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.stripe.android.financialconnections.getMetadata
import com.stripe.android.financialconnections.ui.LocalTopAppBarHost
import com.stripe.android.financialconnections.ui.theme.FinancialConnectionsTheme
import com.stripe.android.financialconnections.utils.TimeZoneRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class PaparazziSampleScreenshotTest {

    @Suppress("DEPRECATION")
    object PreviewProvider : TestParameter.TestParameterValuesProvider {
        override fun provideValues(): List<ComponentTestPreview> {
            val metadata = Showkase.getMetadata()
            return metadata.componentList.map(::ComponentTestPreview)
        }
    }

    @Suppress("Unused")
    enum class BaseDeviceConfig(
        val deviceConfig: DeviceConfig,
    ) {
        NEXUS_5(DeviceConfig.NEXUS_5),
        PIXEL_C(DeviceConfig.PIXEL_C),
    }

    @Suppress("Unused")
    enum class SystemAppearance(val nightMode: NightMode) {
        Light(nightMode = NightMode.NOTNIGHT),
        Dark(nightMode = NightMode.NIGHT),
    }

    @get:Rule
    val timeZoneRule = TimeZoneRule()

    @get:Rule
    val paparazzi = Paparazzi(
        // Needed to shrink the screenshot to the height of the composable
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

    @Test
    fun preview_tests(
        @TestParameter(valuesProvider = PreviewProvider::class) componentTestPreview: ComponentTestPreview,
        @TestParameter baseDeviceConfig: BaseDeviceConfig,
        @TestParameter systemAppearance: SystemAppearance,
    ) {
        paparazzi.unsafeUpdateConfig(
            baseDeviceConfig.deviceConfig.copy(
                softButtons = false,
                nightMode = systemAppearance.nightMode,
            )
        )
        paparazzi.snapshot {
            FinancialConnectionsTheme {
                CompositionLocalProvider(
                    LocalInspectionMode provides true,
                    LocalTopAppBarHost provides FakeTopAppBarHost(),
                    LocalContentColor provides FinancialConnectionsTheme.colors.textDefault,
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(FinancialConnectionsTheme.colors.background),
                    ) {
                        componentTestPreview.Content()
                    }
                }
            }
        }
    }
}

class ComponentTestPreview(
    private val showkaseBrowserComponent: ShowkaseBrowserComponent
) {
    @Composable
    fun Content() {
        showkaseBrowserComponent.component()
    }

    override fun toString(): String {
        // Remove the long prefix to prevent `File name too long` errors
        return showkaseBrowserComponent.componentKey.replace("com.stripe.android.financialconnections.", "")
    }
}
