package com.icloud.ciro.silvano.youtodo

import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.chip.Chip
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /*
    @Test
    fun navigationFromMainToAddTest() {
        // Create a mock NavController
        val mockNavController = mock(NavController::class.java)

        // Create a graphical FragmentScenario for the TitleScreen
        val titleScenario = launchFragmentInContainer<MainFragment>(themeResId = R.style.Base_Theme_YouToDo)

        // Set the NavController property on the fragment
        titleScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // Verify that performing a click prompts the correct Navigation action
        onView(ViewMatchers.withId(R.id.addFAB)).perform(click())
        verify(mockNavController).navigate(R.id.action_mainFragment_to_addFragment)
    }*/

    /**
     * Questo test ha lo scopo di verificare che l'addFragment crei correttamente una card
     * Si testa la creazione di una card con una delle categorie disponibili (creando due card, utili per il prossimo test)
     * ed una card con una nuova categoria
     */
    @Test
    fun addNewCard() {
        //ci si sposta su AddFragment
        onView(withId(R.id.addFAB)).perform(click())

        //si scrive il nome della card (in questo caso Prova1)
        onView(withId(R.id.addName)).perform(typeText("Prova1"), click(), closeSoftKeyboard())

        //per selezionare la data e l'ora la questione è un po' più complessa
        //bisogna innzanzitutto far comparire il dialog relativo in questo caso alla data
        onView(withId(R.id.addDate)).perform(click())

        //fatto ciò, cerchiamo la view ed inseriamo la data
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                2017,
                10,
                11
            ), click()
        )

        //infine, bisogna cliccare sul positive button del dialog (per i dialog autogenerati è button1)
        onView(withId(android.R.id.button1)).perform(click())

        //inserimento dell'ora
        onView(withId(R.id.addTime)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                12,
                10,
            ), click(), closeSoftKeyboard()
        )
        onView(withId(android.R.id.button1)).perform(click())

        //
        onView(allOf(withText(containsString("personal")), isAssignableFrom(Chip::class.java))).perform(click())
        //onView(withId(R.id.editAddCategory)).perform(typeText("new"), click(), closeSoftKeyboard())

        //inserisci la nuova card
        onView(withId(R.id.btnAdd)).perform(click())

        onView(withId(R.id.itemsList)).check(matches(hasChildCount(1)))
        //ripetiamo l'aggiunta per avere una card con categoria diversa per il prossimo test
        onView(withId(R.id.addFAB)).perform(click())

        onView(withId(R.id.addName)).perform(typeText("Prova2"), click(), closeSoftKeyboard())

        onView(withId(R.id.addDate)).perform(click())

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                2022,
                6,
                6
            ), click()
        )

        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.addTime)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                18,
                33,
            ), click(), closeSoftKeyboard()
        )
        onView(withId(android.R.id.button1)).perform(click())

        //
        onView(allOf(withText(containsString("all")), isAssignableFrom(Chip::class.java))).perform(click())

        onView(withId(R.id.btnAdd)).perform(click())


        //long click on the second item of the recyclerView to check if it's been selected
        //onView(withId(R.id.itemsList)).perform(RecyclerViewActions.actionOnItemAtPosition<CardAdapter.MyViewHolder>(1,
        //    longClick()))

        //modo per verificare il numero di elementi della recyclerView
        //onView(withId(R.id.itemsList)).check(matches(hasChildCount(5)))

        onView(withId(R.id.itemsList)).check(matches(hasChildCount(2)))
    }

    @Test
    fun filterCards() {
        onView(withId(R.id.itemsList)).check(matches(hasChildCount(2)))

        onView(allOf(withText("all"), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())
        onView(withId(R.id.itemsList)).check(matches(hasChildCount(1)))

        onView(allOf(withText(containsString("personal")), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())
        onView(withId(R.id.itemsList)).check(matches(hasChildCount(1)))

        /*
        onView(withId(R.id.topAppBar)).perform(click())
        //openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        //openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)

        //openContextualActionModeOverflowMenu()
        //onView(withId(R.id.to_do_nav)).perform(click())
        onView(withId(R.id.itemsList)).check(matches(hasChildCount(2)))
        */
    }

    @Test
    fun editCard() {
        //verifichiamo che la recyclerView sia visibile
        onView(withId(R.id.itemsList)).check(matches(isDisplayed()))

        //clicchiamo sulla prima card per provare a modificarla
        onView(withId(R.id.itemsList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CardAdapter.MyViewHolder>(0, click()))

        //modifica solo del nome della card
        onView(withId(R.id.editName)).perform(clearText(), typeText("Nuovo nome"), click(), closeSoftKeyboard())
        onView(withId(R.id.btnEdit)).perform(click())


        onView(withId(R.id.itemsList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CardAdapter.MyViewHolder>(0, click()))

        //Modifica solo la deadline della card
        onView(withId(R.id.editDate)).perform(click())

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                2022,
                11,
                10
            ), click()
        )

        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.editTime)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                14,
                7,
            ), click(), closeSoftKeyboard()
        )
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.btnEdit)).perform(click())

        onView(withId(R.id.itemsList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CardAdapter.MyViewHolder>(0, click()))

        onView(allOf(withText(containsString("all")), isAssignableFrom(Chip::class.java))).perform(click())
        onView(withId(R.id.btnEdit)).perform(click())
    }

    @Test
    fun deleteCard() {
        //verifichiamo che la recyclerView sia visibile
        onView(withId(R.id.itemsList)).check(matches(isDisplayed()))

        //clicchiamo sulla prima card per provare a modificarla
        onView(withId(R.id.itemsList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CardAdapter.MyViewHolder>(1, click()))

        onView(withId(R.id.deleteEditButton)).perform(click())

        onView(withId(android.R.id.button1)).perform(click())
    }

    @Test
    fun addCategoryFromCategoryFragment() {
        //navigate to CategoryFragment
        onView(withId(R.id.category_nav)).perform(click())

        onView(withId(R.id.catList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CardAdapter.MyViewHolder>(1, click()))

        onView(withId(R.id.FabAddCat)).perform(click())

        onView(withId(R.id.addCategory)).perform(typeText("new cat"), click(), closeSoftKeyboard())
        onView(withId(R.id.confirm)).perform(click())
    }

    @Test
    fun editCategory() {
        //navigazione a CategoryFragment
        onView(withId(R.id.category_nav)).perform(click())

        //click sul pulsante edit della prima voce della recyclerView
        onView(withId(R.id.catList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                object : ViewAction {
                    override fun getConstraints(): Matcher<View>? {
                        return null
                    }

                    override fun getDescription(): String {
                        return "Click on specific button"
                    }

                    override fun perform(uiController: UiController?, view: View) {
                        val button: View = view.findViewById(R.id.btnModifyCat)
                        // Maybe check for null
                        button.performClick()
                    }
                }
            )
        )

        onView(withId(R.id.editCategory)).perform(clearText(), typeText("new value"), click(), closeSoftKeyboard())
        onView(withId(R.id.confirm)).perform(click())
    }

    @Test
    fun deleteCategory() {
        //navigazione a CategoryFragment
        onView(withId(R.id.category_nav)).perform(click())

        //click sul pulsante edit della prima voce della recyclerView
        onView(withId(R.id.catList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                object : ViewAction {
                    override fun getConstraints(): Matcher<View>? {
                        return null
                    }

                    override fun getDescription(): String {
                        return "Click on specific button"
                    }

                    override fun perform(uiController: UiController?, view: View) {
                        val button: View = view.findViewById(R.id.btnDeleteCat)
                        // Maybe check for null
                        button.performClick()
                    }
                }
            )
        )

        onView(withId(android.R.id.button1)).perform(click())
    }

    @Test
    fun themesTest() {
        //navigazione a SettingsFragment
        onView(withId(R.id.settings_nav)).perform(click())

        onView(withId(R.id.btnTheme1)).perform(click())

        onView(withId(R.id.backSettingsButton)).perform(click())

        onView(withId(R.id.settings_nav)).perform(click())

        onView(withId(R.id.btnDarkMode)).perform(click())
        onView(withId(R.id.backSettingsButton)).perform(click())

    }
}
