# AI Text Summarizer

A Java console application that summarizes lengthy text or articles using
[LangChain4j](https://github.com/langchain4j/langchain4j) and an LLM (OpenAI by
default, or any OpenAI-compatible endpoint).

For each input it produces:

- a **bullet-point summary** at the requested depth (short / medium / detailed)
- **important keywords**
- **key takeaways**

## Requirements

- Java 21+
- Maven 3.8+
- An OpenAI API key (or an OpenAI-compatible endpoint)

## Setup

```bash
export OPENAI_API_KEY="sk-..."
# optional:
export OPENAI_MODEL="global.anthropic.claude-sonnet-4-6."        # default
export OPENAI_BASE_URL="https://llmgw-wp.tekstac.com"     # for OpenAI-compatible APIs
```

## Run

### Interactive mode

```bash
mvn compile exec:java
```

Choose a summary length, paste your text, then type `END` on its own line.

### File mode

```bash
mvn compile exec:java -Dexec.args="article.txt detailed"
# or
java -jar target/ai-text-summarizer-1.0.0.jar article.txt short
```

(`mvn package` produces the runnable shaded jar.)

Length argument accepts: `short`/`s`/`1`, `medium`/`m`/`2`, `detailed`/`d`/`3`.
Defaults to `medium`.

## How it works

- `TextSummarizer` is a LangChain4j **AI Service**. Its method returns a
  `SummaryReport` POJO, so LangChain4j requests structured JSON output from the
  model and deserializes it automatically.
- `SummaryLength` injects length-specific guidance into the prompt.
- `App` handles input (console paste or file) and prints the report.

## Project layout

```
src/main/java/com/example/summarizer/
├── App.java              CLI entry point (interactive + file modes)
├── TextSummarizer.java   LangChain4j AI service (prompt + structured output)
├── SummaryReport.java    Output model: bulletPoints, keywords, keyTakeaways
└── SummaryLength.java    SHORT / MEDIUM / DETAILED prompt guidance
```
