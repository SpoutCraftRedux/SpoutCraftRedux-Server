package org.spoutcraftredux.server.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.spoutcraftredux.api.gui.Button;
import org.spoutcraftredux.api.gui.CheckBox;
import org.spoutcraftredux.api.gui.Gradient;
import org.spoutcraftredux.api.gui.ItemWidget;
import org.spoutcraftredux.api.gui.Label;
import org.spoutcraftredux.api.gui.ListWidget;
import org.spoutcraftredux.api.gui.Slider;
import org.spoutcraftredux.api.gui.TextAlign;
import org.spoutcraftredux.api.gui.TextField;
import org.spoutcraftredux.api.gui.Widget;
import org.spoutcraftredux.protocol.gui.WidgetSpec;
import org.spoutcraftredux.protocol.gui.WidgetTypes;
import org.spoutcraftredux.server.network.Payloads;

/** Turns API widgets into the descriptions sent to the client. */
public final class WidgetSpecs {
    private WidgetSpecs() {
    }

    /**
     * Describes a widget for the client.
     *
     * @param widget the widget
     * @return its description
     * @throws IllegalArgumentException if the widget is not one of the API's widget types
     */
    public static WidgetSpec of(final Widget<?> widget) {
        final Map<String, Object> properties = new HashMap<>();
        final String type = switch (widget) {
            case Label label -> {
                properties.put("text", Payloads.json(label.getText()));
                properties.put("color", label.getColor());
                properties.put("shadow", label.hasShadow());
                properties.put("wrap", label.isWrap());
                properties.put("align", align(label.getAlign()));
                yield WidgetTypes.LABEL;
            }
            case Button button -> {
                properties.put("text", Payloads.json(button.getText()));
                properties.put("align", align(button.getAlign()));
                yield WidgetTypes.BUTTON;
            }
            case CheckBox box -> {
                properties.put("text", Payloads.json(box.getText()));
                properties.put("checked", box.isChecked());
                yield WidgetTypes.CHECKBOX;
            }
            case TextField field -> {
                properties.put("text", field.getText());
                properties.put("placeholder", Payloads.json(field.getPlaceholder()));
                properties.put("max_length", field.getMaxLength());
                yield WidgetTypes.TEXT_FIELD;
            }
            case Slider slider -> {
                properties.put("text", Payloads.json(slider.getText()));
                properties.put("position", slider.getPosition());
                yield WidgetTypes.SLIDER;
            }
            case Gradient gradient -> {
                properties.put("top", gradient.getTopColor());
                properties.put("bottom", gradient.getBottomColor());
                yield WidgetTypes.GRADIENT;
            }
            case ItemWidget item -> {
                properties.put("item", item.getItem().getKey().asString());
                yield WidgetTypes.ITEM;
            }
            case ListWidget list -> {
                final List<String> titles = new ArrayList<>();
                final List<String> texts = new ArrayList<>();
                for (final ListWidget.Row row : list.getRows()) {
                    titles.add(Payloads.json(row.title()));
                    texts.add(Payloads.json(row.text()));
                }
                properties.put("titles", titles);
                properties.put("texts", texts);
                properties.put("selected", list.getSelected());
                yield WidgetTypes.LIST;
            }
            default -> throw new IllegalArgumentException("Unknown widget type " + widget.getClass().getName());
        };
        return new WidgetSpec(widget.getId(), type, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(),
            widget.getAnchor().ordinal(), widget.isVisible(), widget.isEnabled(),
            widget.getTooltip() == null ? "" : Payloads.json(widget.getTooltip()), properties);
    }

    private static int align(final TextAlign align) {
        return align.ordinal();
    }
}
