
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/** Displays a dialog, which is a modal window containing a content table with a button table underneath it. Methods are provided
 * to add a label to the content table and buttons to the button table, but any widgets can be added. When a button is clicked,
 * {@link #clicked(Object)} is called and the dialog is removed from the stage.
 * @author Nathan Sweet */
public class Dialog extends Window {
	final Table contentTable, buttonTable;
	private Skin skin;
	ObjectMap<Actor, Object> values = new ObjectMap();

	public Dialog (String title, Skin skin) {
		this(title, skin.get(WindowStyle.class));
		this.skin = skin;
	}

	public Dialog (String title, Skin skin, String windowStyleName) {
		this(title, skin.get(windowStyleName, WindowStyle.class));
		this.skin = skin;
	}

	public Dialog (String title, WindowStyle windowStyle) {
		super(title, windowStyle);

		setModal(true);

		defaults().space(6);
		add(contentTable = new Table(skin)).expand();
		row();
		add(buttonTable = new Table(skin));

		contentTable.defaults().space(6);
		buttonTable.defaults().space(6);

		buttonTable.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				while (actor.getParent() != buttonTable)
					actor = actor.getParent();
				clicked(values.get(actor));
				hide();
			}
		});
	}

	public Table getContentTable () {
		return contentTable;
	}

	public Table getButtonTable () {
		return buttonTable;
	}

	/** Adds a label to the content table. The dialog must have been constructed with a skin to use this method. */
	public Dialog text (String text) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
		return text(text, skin.get(LabelStyle.class));
	}

	/** Adds a label to the content table. */
	public Dialog text (String text, LabelStyle labelStyle) {
		contentTable.add(new Label(text, labelStyle));
		return this;
	}

	/** Adds a text button to the button table. Null will be passed to {@link #clicked(Object)} if this button is clicked. The
	 * dialog must have been constructed with a skin to use this method. */
	public Dialog button (String text) {
		return button(text, null);
	}

	/** Adds a text button to the button table. The dialog must have been constructed with a skin to use this method.
	 * @param object The object that will be passed to {@link #clicked(Object)} if this button is clicked. May be null. */
	public Dialog button (String text, Object object) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
		return button(text, object, skin.get(TextButtonStyle.class));
	}

	/** Adds a text button to the button table.
	 * @param object The object that will be passed to {@link #clicked(Object)} if this button is clicked. May be null. */
	public Dialog button (String text, Object object, TextButtonStyle buttonStyle) {
		TextButton button = new TextButton(text, buttonStyle);
		buttonTable.add(button);
		setObject(button, object);
		return this;
	}

	/** {@link #pack() Packs} the dialog and adds it to the stage, centered. */
	public Dialog show (Stage stage) {
		return show(stage, 0);
	}

	/** {@link #pack() Packs} the dialog and adds it to the stage, centered.
	 * @param duration If > 0, the dialog will fade in. */
	public Dialog show (Stage stage, float duration) {
		stage.setKeyboardFocus(this);
		stage.setScrollFocus(this);
		pack();
		setPosition((stage.getWidth() - getWidth()) / 2, (stage.getHeight() - getHeight()) / 2);
		stage.addActor(this);
		if (duration > 0) {
			getColor().a = 0;
			addAction(Actions.fadeIn(duration, Interpolation.fade));
		}
		return this;
	}

	/** Hides the dialog. Called automatically when a button is clicked. The default implementation fades out the dialog over 0.4
	 * seconds and then removes it from the stage. */
	public void hide () {
		addAction(sequence(fadeOut(0.4f, Interpolation.fade), Actions.removeActor()));
	}

	public void setObject (Actor actor, Object object) {
		values.put(actor, object);
	}

	/** Called when a button is clicked.
	 * @param object The object specified when the button was added. */
	protected void clicked (Object object) {
	}
}
