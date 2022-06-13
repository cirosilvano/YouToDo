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
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


/**
 * Classe che mira a testare le operazioni più comuni fatte utilizzando l'applicazione
 *
 * REQUISITI (IMPORTANTE):
 * 1.   Siccome espresso potrebbe avere problemi nel trovare le View se si usano animazioni o transizioni,
 *      è NECESSARIO disattivarle. Per fare ciò, è sufficiente (supponendo che la lingua di sistema sia inglese)
 *      andare su settings -> developer options e nella sezione Drawing disabilitare:
 *      - window animation scale;
 *      - transition animation scale;
 *      - animator duration scale.
 *
 * 2.   Inoltre, è NECESSARIO che, prima dell'esecuzione dei test, l'applicazione abbia
 *      le categorie "personal" e "work".
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class ToDoTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

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

        onView(allOf(withText(containsString("all")), isAssignableFrom(Chip::class.java))).perform(click())

        onView(withId(R.id.btnAdd)).perform(click())

        onView(withId(R.id.itemsList)).check(matches(hasChildCount(2)))
    }

    /**
     * Questo test ha lo scopo di verificare che premere sui chip relativi ad una certa categoria
     * filtri correttamente le card in modo da mostrare solo quelle appartenenti alla data categoria
     */
    @Test
    fun filterCardsByCategory() {
        //provo a filtrare le card che hanno la categoria personal
        onView(allOf(withText(containsString("personal")), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())

        //controllo che ripremendo il chip si deselezioni e ricompaiano tutte le card
        onView(allOf(withText(containsString("personal")), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())

        onView(allOf(withText(containsString("new value")), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())

        //controllo che ripremendo il chip si deselezioni e ricompaiano tutte le card
        onView(allOf(withText(containsString("new value")), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())

        onView(allOf(withText(containsString("personal")), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())
        onView(allOf(withText(containsString("new value")), not(withId(R.id.itemCategory)), isAssignableFrom(Chip::class.java))).perform(click())
    }

    /**
     * Questo test ha lo scopo di verificare che premere su una card permetta di passare
     * ad EditFragment e che la modifica di uno dei valori (nome, categoria o deadline) venga
     * correttamente applicato una volta premuto sul bottone per la conferma delle modifiche
     */
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

        onView(allOf(withText(containsString("personal")), isAssignableFrom(Chip::class.java))).perform(click())
        onView(withId(R.id.btnEdit)).perform(click())
    }

    /**
     * Questo test ha lo scopo di verificare che le card siano correttamente eliminate quando si usa
     * l'ImageButton presente nell'EditFragment relativo alla card
     */
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

    /**
     * Questo test ha lo scopo di verificare che creare una categoria tramite categoryFragment
     * funzioni correttamente
     */
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

    /**
     * Questo test ha lo scopo di verificare la corretta modifica di una categoria
     * tramite l'ImageButton specifico a destra del nome della categoria
     */
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

    /**
     * Questo test ha lo scopo di verificare la corretta eliminazione di una categoria
     * tramite l'ImageButton specifico a destra del nome della categoria
     */
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

    /**
     * Questo test ha lo scopo di verificare la corretta applicazione del tema scelto
     * in SettingFragment all'applicazione
     */
    @Test
    fun themesTest() {
        //navigazione a SettingsFragment
        onView(withId(R.id.settings_nav)).perform(click())

        onView(withId(R.id.btnTheme1)).perform(click())

        onView(withId(R.id.home_nav)).perform(click())
        onView(withId(R.id.category_nav)).perform(click())
        onView(withId(R.id.settings_nav)).perform(click())

        onView(withId(R.id.btnTheme2)).perform(click())

        onView(withId(R.id.home_nav)).perform(click())
        onView(withId(R.id.category_nav)).perform(click())
        onView(withId(R.id.settings_nav)).perform(click())

        onView(withId(R.id.btnDefault)).perform(click())

        onView(withId(R.id.home_nav)).perform(click())
        onView(withId(R.id.category_nav)).perform(click())
        onView(withId(R.id.settings_nav)).perform(click())

        onView(withId(R.id.btnDarkMode)).perform(click())

        onView(withId(R.id.home_nav)).perform(click())
        onView(withId(R.id.category_nav)).perform(click())
        onView(withId(R.id.settings_nav)).perform(click())

        onView(withId(R.id.btnLightMode)).perform(click())

        onView(withId(R.id.home_nav)).perform(click())
        onView(withId(R.id.category_nav)).perform(click())

    }
}
