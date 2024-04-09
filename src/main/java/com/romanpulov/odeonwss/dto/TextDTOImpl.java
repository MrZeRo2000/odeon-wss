package com.romanpulov.odeonwss.dto;

public class TextDTOImpl implements TextDTO {
    private String text;

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static TextDTOImpl fromText(String text) {
        TextDTOImpl instance = new TextDTOImpl();
        instance.setText(text);

        return instance;
    }

    @Override
    public String toString() {
        return "TextDTOImpl{" +
                "text='" + getText() + '\'' +
                '}';
    }
}
