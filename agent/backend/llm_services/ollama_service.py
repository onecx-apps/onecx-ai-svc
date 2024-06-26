import os
import requests
from typing import Any, Dict, List, Tuple, Union
from dotenv import load_dotenv
from loguru import logger
from langchain.docstore.document import Document as LangchainDocument
from agent.utils.utility import replace_multiple_whitespaces, convert_message
from langchain.schema import BaseMessage, HumanMessage, AIMessage, SystemMessage
from agent.backend.llm_services.LLM import BaseLLM 
import ollama
from ollama import Client


load_dotenv()

OLLAMA_URL = os.getenv("OLLAMA_URL")
OLLAMA_API_KEY = os.getenv('OLLAMA_API_KEY')
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "mixtral")
OLLAMA_MODEL_VERSION = os.getenv('OLLAMA_MODEL_VERSION')

EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL")

client = Client(host=OLLAMA_URL, auth=("API_KEY", OLLAMA_API_KEY))


ollama_model = f"{OLLAMA_MODEL}:{OLLAMA_MODEL_VERSION}" if OLLAMA_MODEL_VERSION else OLLAMA_MODEL


class OllamaLLM(BaseLLM):
    def __init__(self):
        pass

    def chat(self, documents: list[tuple[LangchainDocument, float]], messages: any, query: str) -> Tuple[str, Union[Dict[Any, Any], List[Dict[Any, Any]]]]:
        """Takes a list of documents and returns a list of answers.

        Args:
            documents (List[Tuple[Document, float]]): A list of tuples containing the document and its relevance score.
            query (str): The query to ask.
            summarization (bool, optional): Whether to use summarization. Defaults to False.

        Returns:
            Tuple[str, str, Union[Dict[Any, Any], List[Dict[Any, Any]]]]: A tuple containing the answer, the prompt, and the metadata for the documents.
        """
        text = ""
        # extract the texts and meta data from the documents
        texts = [replace_multiple_whitespaces(doc.page_content) for doc in documents]
        text = " ".join(texts)
        meta_data = [doc.metadata for doc in documents]

        answer=""
        try:
            # fills the prompt, sends a request and returns the response
            #answer = self.send_chat_completion(text=text, query=query, conversation_type=conversation_type, messages=messages)

            


            # use the query and the found document texts and create a message with question and context
            message = convert_message(query, text)
            messages[0].append(message)

            logger.debug(f"Message history: {messages}")

            llm_response = client.chat(model=ollama_model, 
                messages=messages[0],
                stream=False,
            )

            answer = llm_response['message']['content']


        except ValueError as e:
            logger.error("Error found:")
            logger.error(e)
            answer = "Error while processing the completion"
        logger.debug(f"LLM response: {answer}")
        
        return answer, meta_data


    def generate(self, text: str) -> str:
        """Takes a text.

        Args:
            text (str): The text to use for generation.

        Returns:
            result (str)
        """
        result=""
        
        try:
            # fills the prompt, sends a request and returns the response
            llm_response = client.generate(model=ollama_model, 
                prompt=text,
                stream=False,
            )
            answer = llm_response['response']

        except ValueError as e:
            logger.error("Error found:")
            logger.error(e)
            result = "Error while processing the generation"
        logger.debug(f"LLM response: {answer}")
        
        return answer


