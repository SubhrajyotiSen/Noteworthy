package me.subhrajyoti.noteworthy;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import me.subhrajyoti.noteworthy.Activities.MainActivity;
import me.subhrajyoti.noteworthy.Adapters.MainViewHolder;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by nestorkokoafantchao on 10/27/17.
 */
@RunWith(AndroidJUnit4.class)
public class ActionFlowTest {
    private final String TITLE="Shope in the wood";
    private final String DESCRIPTION="This is how you creat new Note ";


    @Rule
    public ActivityTestRule<MainActivity> myMainActivityTest = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void CreateNewNote(){

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.title_text))
               .perform(ViewActions.typeText(DESCRIPTION),ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withText(R.id.fab)).perform(ViewActions.click());

       // Espresso.onView(ViewMatchers.withId(R.id.recyclerView)).check()


    }
    private static Matcher<RecyclerView.ViewHolder> isInTheMiddle(final String text) {
        return new BoundedMatcher<RecyclerView.ViewHolder,MainViewHolder>(MainViewHolder.class) {

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("No ViewHolder found with text: " + text);
            }

            @Override
            protected boolean matchesSafely(MainViewHolder item) {
                TextView textView= (TextView) item.itemView.findViewById(R.id.title_text);
                if(textView==null)
                    return  false ;

                return textView.getText().toString().contains(text);
            }
        };

    }


}
