package com.example.summarizer;

import dev.langchain4j.model.output.structured.Description;

import java.util.List;

/**
 * Structured output returned by the summarizer AI service.
 */
public class SummaryReport {

    @Description("bullet points that form the summary; one distinct idea per item, no leading '-' or bullet characters")
    private List<String> bulletPoints;

    @Description("the most important keywords and phrases from the text, 5 to 10 items")
    private List<String> keywords;

    @Description("the key takeaways a reader should remember after reading the text, 3 to 5 items")
    private List<String> keyTakeaways;

    public List<String> getBulletPoints() {
        return bulletPoints;
    }

    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getKeyTakeaways() {
        return keyTakeaways;
    }

    public void setKeyTakeaways(List<String> keyTakeaways) {
        this.keyTakeaways = keyTakeaways;
    }
}
