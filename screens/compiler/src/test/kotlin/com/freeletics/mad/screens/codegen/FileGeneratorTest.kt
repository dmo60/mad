package com.freeletics.mad.screens.codegen

import com.freeletics.mad.screens.Extra
import com.freeletics.mad.screens.Navigation
import com.freeletics.mad.screens.ScreenData
import com.squareup.kotlinpoet.ClassName
import io.kotest.matchers.shouldBe
import org.junit.Test

class FileGeneratorTest {

    private val scopeClass = ClassName("com.test", "Test")

    private val full = ScreenData(
        parentScope = ClassName("com.test.parent", "TestParentScope"),
        dependencies = ClassName("com.test", "TestDependencies"),
        stateMachine = ClassName("com.test", "TestStateMachine"),
        navigation = Navigation(
            navigator = ClassName("com.test", "TestNavigator"),
            navigationHandler = ClassName("com.test.navigation", "TestNavigationHandler"),
        ),
        coroutinesEnabled = true,
        rxJavaEnabled = true,
        extra = null
    )

    @Test
    fun `generates code for full ScreenData`() {
        val generator = FileGenerator(scopeClass, full)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.navigation.TestNavigationHandler
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              public val testNavigator: TestNavigator

              public val testNavigationHandler: TestNavigationHandler

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData without navigation`() {
        val withoutNavigation = full.copy(navigation = null)
        val generator = FileGenerator(scopeClass, withoutNavigation)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.squareup.anvil.annotations.MergeComponent
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData without coroutines`() {
        val withoutCoroutines = full.copy(coroutinesEnabled = false)
        val generator = FileGenerator(scopeClass, withoutCoroutines)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.navigation.TestNavigationHandler
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.Unit

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              public val testNavigator: TestNavigator

              public val testNavigationHandler: TestNavigationHandler

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable)

              public override fun onCleared(): Unit {
                disposable.clear()
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData without rxjava`() {
        val withoutRxJava = full.copy(rxJavaEnabled = false)
        val generator = FileGenerator(scopeClass, withoutRxJava)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.navigation.TestNavigationHandler
            import dagger.BindsInstance
            import dagger.Component
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              public val testNavigator: TestNavigator

              public val testNavigationHandler: TestNavigationHandler

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  scope)

              public override fun onCleared(): Unit {
                scope.cancel()
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData with compose`() {
        val without = full.copy(
            extra = Extra.Compose(withFragment = false)
        )
        val generator = FileGenerator(scopeClass, without)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.LaunchedEffect
            import androidx.compose.runtime.collectAsState
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import androidx.navigation.NavController
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.freeletics.mad.screens.`internal`.rememberViewModelProvider
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.navigation.TestNavigationHandler
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel
            import kotlinx.coroutines.launch

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              public val testNavigator: TestNavigator

              public val testNavigationHandler: TestNavigationHandler

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }

            @Composable
            @OptIn(InternalScreensApi::class)
            public fun Test(navController: NavController): Unit {
              val scope = rememberCoroutineScope()

              val viewModelProvider = rememberViewModelProvider<TestDependencies>(TestParentScope::class) {
                  dependencies, handle -> 
                val arguments = navController.currentBackStackEntry!!.arguments ?: Bundle.EMPTY
                TestViewModel(dependencies, handle, arguments)
              }
              val viewModel = viewModelProvider[TestViewModel::class.java]
              val component = viewModel.component

              LaunchedEffect(scope, navController, component) {
                val handler = component.testNavigationHandler
                val navigator = component.testNavigator
                handler.handle(scope, navController, navigator)
              }

              val stateMachine = component.testStateMachine
              val state = stateMachine.state.collectAsState()
              TestUi(state.value) { action ->
                scope.launch { stateMachine.dispatch(action) }
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData with compose, no navigation`() {
        val without = full.copy(
            navigation = null,
            extra = Extra.Compose(withFragment = false)
        )
        val generator = FileGenerator(scopeClass, without)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.collectAsState
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.freeletics.mad.screens.`internal`.rememberViewModelProvider
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel
            import kotlinx.coroutines.launch

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }

            @Composable
            @OptIn(InternalScreensApi::class)
            public fun Test(): Unit {
              val scope = rememberCoroutineScope()

              val viewModelProvider = rememberViewModelProvider<TestDependencies>(TestParentScope::class) {
                  dependencies, handle -> 
                val arguments = navController.currentBackStackEntry!!.arguments ?: Bundle.EMPTY
                TestViewModel(dependencies, handle, arguments)
              }
              val viewModel = viewModelProvider[TestViewModel::class.java]
              val component = viewModel.component

              val stateMachine = component.testStateMachine
              val state = stateMachine.state.collectAsState()
              TestUi(state.value) { action ->
                scope.launch { stateMachine.dispatch(action) }
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData with compose fragment`() {
        val withComposeFragment = full.copy(
            extra = Extra.Compose(withFragment = true)
        )
        val generator = FileGenerator(scopeClass, withComposeFragment)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.LaunchedEffect
            import androidx.compose.runtime.collectAsState
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import androidx.navigation.NavController
            import androidx.navigation.fragment.findNavController
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.freeletics.mad.screens.`internal`.rememberViewModelProvider
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.navigation.TestNavigationHandler
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel
            import kotlinx.coroutines.launch

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              public val testNavigator: TestNavigator

              public val testNavigationHandler: TestNavigationHandler

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }

            @Composable
            @OptIn(InternalScreensApi::class)
            public fun Test(navController: NavController): Unit {
              val scope = rememberCoroutineScope()

              val viewModelProvider = rememberViewModelProvider<TestDependencies>(TestParentScope::class) {
                  dependencies, handle -> 
                val arguments = navController.currentBackStackEntry!!.arguments ?: Bundle.EMPTY
                TestViewModel(dependencies, handle, arguments)
              }
              val viewModel = viewModelProvider[TestViewModel::class.java]
              val component = viewModel.component

              LaunchedEffect(scope, navController, component) {
                val handler = component.testNavigationHandler
                val navigator = component.testNavigator
                handler.handle(scope, navController, navigator)
              }

              val stateMachine = component.testStateMachine
              val state = stateMachine.state.collectAsState()
              TestUi(state.value) { action ->
                scope.launch { stateMachine.dispatch(action) }
              }
            }
            
            public class TestFragment : Fragment() {
              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
              ): View {
                val navController = findNavController()
                val composeView = ComposeView(requireContext())
                composeView.setContent {
                  Test(navController)
                }
                return composeView
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData with compose fragment, no navigation`() {
        val withComposeFragmentNoNavigation = full.copy(
            navigation = null,
            extra = Extra.Compose(withFragment = true)
        )
        val generator = FileGenerator(scopeClass, withComposeFragmentNoNavigation)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.collectAsState
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.freeletics.mad.screens.`internal`.rememberViewModelProvider
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel
            import kotlinx.coroutines.launch

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }

            @Composable
            @OptIn(InternalScreensApi::class)
            public fun Test(): Unit {
              val scope = rememberCoroutineScope()

              val viewModelProvider = rememberViewModelProvider<TestDependencies>(TestParentScope::class) {
                  dependencies, handle -> 
                val arguments = navController.currentBackStackEntry!!.arguments ?: Bundle.EMPTY
                TestViewModel(dependencies, handle, arguments)
              }
              val viewModel = viewModelProvider[TestViewModel::class.java]
              val component = viewModel.component

              val stateMachine = component.testStateMachine
              val state = stateMachine.state.collectAsState()
              TestUi(state.value) { action ->
                scope.launch { stateMachine.dispatch(action) }
              }
            }
            
            public class TestFragment : Fragment() {
              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
              ): View {
                val composeView = ComposeView(requireContext())
                composeView.setContent {
                  Test()
                }
                return composeView
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData with renderer`() {
        val withRenderer = full.copy(
            extra = Extra.Renderer(factory = ClassName("com.test", "RendererFactory"))
        )
        val generator = FileGenerator(scopeClass, withRenderer)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import androidx.lifecycle.coroutineScope
            import androidx.navigation.fragment.findNavController
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.freeletics.mad.screens.`internal`.viewModelProvider
            import com.gabrielittner.renderer.connect.connect
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.navigation.TestNavigationHandler
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine

              public val testNavigator: TestNavigator

              public val testNavigationHandler: TestNavigationHandler
            
              public val rendererFactory: RendererFactory

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }
            
            @OptIn(InternalScreensApi::class)
            public class TestFragment : Fragment() {
              private lateinit var rendererFactory: RendererFactory

              private lateinit var testStateMachine: TestStateMachine

              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
              ): View {
                if (!::testStateMachine.isInitialized) {
                  inject()
                }
                val renderer = rendererFactory.inflate(inflater, container)
                connect(renderer, testStateMachine)
                return renderer.rootView
              }

              private fun inject(): Unit {
                val viewModelProvider = viewModelProvider<TestDependencies>(this, TestParentScope::class) {
                    dependencies, handle -> 
                  val arguments = arguments ?: Bundle.EMPTY
                  TestViewModel(dependencies, handle, arguments)
                }
                val viewModel = viewModelProvider[TestViewModel::class.java]
                val component = viewModel.component

                rendererFactory = component.rendererFactory
                testStateMachine = component.testStateMachine

                val handler = component.testNavigationHandler
                val navigator = component.testNavigator
                val scope = lifecycle.coroutineScope
                val navController = findNavController()
                handler.handle(scope, navController, navigator)
              }
            }
            
        """.trimIndent()
    }

    @Test
    fun `generates code for ScreenData with renderer, no navigation`() {
        val withRendererNoNavigation = full.copy(
            navigation = null,
            extra = Extra.Renderer(factory = ClassName("com.test", "RendererFactory"))
        )
        val generator = FileGenerator(scopeClass, withRendererNoNavigation)

        generator.generate().toString() shouldBe """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.screens.ScopeTo
            import com.freeletics.mad.screens.`internal`.InternalScreensApi
            import com.freeletics.mad.screens.`internal`.viewModelProvider
            import com.gabrielittner.renderer.connect.connect
            import com.squareup.anvil.annotations.MergeComponent
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Component
            import io.reactivex.disposables.CompositeDisposable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.cancel

            @InternalScreensApi
            @ScopeTo(Test::class)
            @MergeComponent(
              scope = Test::class,
              dependencies = [TestDependencies::class]
            )
            internal interface RetainedTestComponent {
              public val testStateMachine: TestStateMachine
            
              public val rendererFactory: RendererFactory

              @Component.Factory
              public interface Factory {
                public fun create(
                  dependencies: TestDependencies,
                  @BindsInstance savedStateHandle: SavedStateHandle,
                  @BindsInstance arguments: Bundle,
                  @BindsInstance compositeDisposable: CompositeDisposable,
                  @BindsInstance coroutineScope: CoroutineScope
                ): RetainedTestComponent
              }
            }

            @InternalScreensApi
            internal class TestViewModel(
              dependencies: TestDependencies,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle
            ) : ViewModel() {
              private val disposable: CompositeDisposable = CompositeDisposable()

              private val scope: CoroutineScope = MainScope()

              public val component: RetainedTestComponent =
                  DaggerRetainedTestComponent.factory().create(dependencies, savedStateHandle, arguments,
                  disposable, scope)

              public override fun onCleared(): Unit {
                disposable.clear()
                scope.cancel()
              }
            }
            
            @OptIn(InternalScreensApi::class)
            public class TestFragment : Fragment() {
              private lateinit var rendererFactory: RendererFactory

              private lateinit var testStateMachine: TestStateMachine

              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
              ): View {
                if (!::testStateMachine.isInitialized) {
                  inject()
                }
                val renderer = rendererFactory.inflate(inflater, container)
                connect(renderer, testStateMachine)
                return renderer.rootView
              }

              private fun inject(): Unit {
                val viewModelProvider = viewModelProvider<TestDependencies>(this, TestParentScope::class) {
                    dependencies, handle -> 
                  val arguments = arguments ?: Bundle.EMPTY
                  TestViewModel(dependencies, handle, arguments)
                }
                val viewModel = viewModelProvider[TestViewModel::class.java]
                val component = viewModel.component

                rendererFactory = component.rendererFactory
                testStateMachine = component.testStateMachine
              }
            }
            
        """.trimIndent()
    }
}