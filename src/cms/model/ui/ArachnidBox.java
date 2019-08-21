package cms.model.ui;

import javafx.scene.control.Button;

import static cms.engine.helpers.FXWindowTools.setStaticSize;

public class ArachnidBox extends Button {
    public ArachnidBox() {
        setStaticSize(70.0,70.0, this);
            /*ImageView spiderIcon = new ImageView(new Image(getClass()
                    .getResource("/resources/images/minispider.jpg").toExternalForm(),
                    70, 70, true, true));

            setGraphic(spiderIcon);*/
        setActive();
    }

    public void setActive() {
        setStyle("-fx-background-color:darkgreen");
    }

    public void setInactive() {
        setStyle("-fx-background-color:dimgrey");
    }

    public void display(String inp) {
        setText(inp);
    }
}
