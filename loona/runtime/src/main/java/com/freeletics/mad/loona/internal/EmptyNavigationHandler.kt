package com.freeletics.mad.loona.internal

import androidx.navigation.NavController
import com.freeletics.mad.loona.Compose
import com.freeletics.mad.loona.RendererFragment
import com.freeletics.mad.navigator.NavigationHandler
import com.freeletics.mad.navigator.Navigator
import kotlinx.coroutines.CoroutineScope

/**
 * Default value for the `navigationHandler` parameter of [Compose] and [RendererFragment].
 * When the generator finds this class as value it will skip generating navigation related code.
 * This allows consumers to not use [Navigator] and [NavigationHandler] based implementations and
 * just have the standard state machine and ui setup in the generated Fragment.
 */
internal class EmptyNavigationHandler : NavigationHandler<EmptyNavigator> {
    init {
        throw UnsupportedOperationException("This is a marker class that should never be used")
    }

    override fun handle(scope: CoroutineScope, navController: NavController, navigator: EmptyNavigator) {
        throw UnsupportedOperationException("This is a marker class that should never be used")
    }
}
