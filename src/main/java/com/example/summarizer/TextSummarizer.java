package com.example.summarizer;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * LangChain4j AI service. Returning a {@link SummaryReport} makes LangChain4j
 * request structured (JSON) output from the model and deserialize it automatically.
 */
public interface TextSummarizer {

    @SystemMessage("""
            You are an expert summarization assistant. Analyze the user's text and
            produce a clear, easy-to-understand report containing:
            - a bullet-point summary
            - the most important keywords
            - the key takeaways

            Rules:
            - Base everything strictly on the provided text; never invent facts.
            - Write in plain language; expand acronyms on first use.
            - Keep each bullet point focused on a single idea.
            """)
    @UserMessage("""
            Summarize the following text. Requested summary length: {{length}}.

            {{guidance}}

            TEXT:
            {{text}}
            """)
    SummaryReport summarize(
            @V("length") String length,
            @V("guidance") String guidance,
            @V("text") String text
    );
}
