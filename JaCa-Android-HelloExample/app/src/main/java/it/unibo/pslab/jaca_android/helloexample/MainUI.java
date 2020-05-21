package it.unibo.pslab.jaca_android.helloexample;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import it.unibo.pslab.jaca_android.core.ActivityArtifact;
import it.unibo.pslab.jaca_android.core.JaCaBaseActivity;

import android.widget.EditText;
import android.widget.TextView;

public class MainUI extends ActivityArtifact {
    public static class MainActivity extends JaCaBaseActivity {}

    private static final String NEW_INPUT_OBS_PROP = "new_ui_input";

    private TextView console;
    private EditText inputField;

    public void init() {
        super.init(MainActivity.class, R.layout.activity_main, true);

        defineObsProperty(NEW_INPUT_OBS_PROP, "");
    }

    @INTERNAL_OPERATION
    protected void setup() {
        initUI();
    }

    private void initUI(){
        execute(() -> {
            console = (TextView) findUIElement(R.id.consoleTextView);
            inputField = (EditText) findUIElement(R.id.editText1);
        });

        initAppendButton();
    }

    private void initAppendButton(){
        execute(() ->{
            findUIElement(R.id.appendButton).setOnClickListener(v -> {
                final String text = inputField.getText().toString();
                console.append("\n > " + text);
                inputField.setText("");

                beginExternalSession();
                updateObsProperty(NEW_INPUT_OBS_PROP, text);
                endExternalSession(true);
            });
        });
    }

    @OPERATION
    public void writeOnConsole(String agentId, String text){
        execute(() -> {
            console.append("\n["+agentId+"] " + text);
        });
    }
}
