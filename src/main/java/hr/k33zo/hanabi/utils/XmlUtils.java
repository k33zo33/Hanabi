package hr.k33zo.hanabi.utils;

import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.Card;
import hr.k33zo.hanabi.model.GameMove;
import hr.k33zo.hanabi.model.MoveType;
import hr.k33zo.hanabi.model.Player;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class XmlUtils {

    private static final String FILE_NAME = "xml/gameMoves.xml";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss:SSS");

    public static void saveGameMove(GameMove gameMove) {

        List<GameMove> gameMoveList = new ArrayList<>();

        if(Files.exists(Path.of(FILE_NAME))) {
            gameMoveList.addAll(XmlUtils.readAllGameMoves());
        }
        gameMoveList.add(gameMove);

        try {
            Document document = createDocument("gameMoves");
            for(GameMove gm : gameMoveList) {

                Element gameMoveElement = document.createElement("gameMove");
                document.getDocumentElement().appendChild(gameMoveElement);


                gameMoveElement.appendChild(createElement(document, "action_type", gm.getMoveType().toString()));
                gameMoveElement.appendChild(createElement(document, "currentPlayerIndex", String.valueOf(gm.getCurrentPlayerIndex())));
                gameMoveElement.appendChild(createElement(document, "fuses", String.valueOf(gm.getFuses())));
                gameMoveElement.appendChild(createElement(document, "tips", String.valueOf(gm.getTips())));
                gameMoveElement.appendChild(createElement(document, "dateTime", gm.getDateTime().format(dateTimeFormatter)));


                Element playersElement = document.createElement("players");
                for (Player player : gm.getPlayers()) {
                    Element playerElement = document.createElement("player");


                    List<String> cardRepresentations = convertCardsToXmlStrings(player.getHand());
                    for (String cardRepresentation : cardRepresentations) {
                        playerElement.appendChild(createElement(document, "card", cardRepresentation));
                    }
                    playersElement.appendChild(playerElement);
                }
                gameMoveElement.appendChild(playersElement);

                Element discardPileElement = document.createElement("discardPile");
                for (Card card : gm.getDiscardPile()) {
                    discardPileElement.appendChild(createElement(document, "card", card.toString()));
                }
                gameMoveElement.appendChild(discardPileElement);

                Element fireworksElement = document.createElement("fireworks");
                for (Map.Entry<Suit, Integer> entry : gm.getFireworks().entrySet()) {
                    Element suitElement = document.createElement("suit");
                    suitElement.appendChild(document.createTextNode(entry.getKey().toString())); // Modify based on Suit's toString()
                    fireworksElement.appendChild(suitElement);

                    Element countElement = document.createElement("count");
                    countElement.appendChild(document.createTextNode(String.valueOf(entry.getValue())));
                    fireworksElement.appendChild(countElement);
                }
                gameMoveElement.appendChild(fireworksElement);


            }
            saveDocument(document, FILE_NAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static List<GameMove> readAllGameMoves() {
        List<GameMove> gameMoves = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(FILE_NAME));

            doc.getDocumentElement().normalize();

            NodeList gameMoveNodes = doc.getElementsByTagName("gameMove");

            for (int i = 0; i < gameMoveNodes.getLength(); i++) {
                Node gameMoveNode = gameMoveNodes.item(i);
                if (gameMoveNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element gameMoveElement = (Element) gameMoveNode;


                    String actionType = getTextContent(gameMoveElement, "action_type");
                    int currentPlayerIndex = Integer.parseInt(getTextContent(gameMoveElement, "currentPlayerIndex"));
                    int fuses = Integer.parseInt(getTextContent(gameMoveElement, "fuses"));
                    int tips = Integer.parseInt(getTextContent(gameMoveElement, "tips"));
                    LocalDateTime dateTime = LocalDateTime.parse(getTextContent(gameMoveElement, "dateTime"), dateTimeFormatter);

                    System.out.println("Reading gameMove: actionType=" + actionType + ", currentPlayerIndex=" + currentPlayerIndex +
                            ", fuses=" + fuses + ", tips=" + tips + ", dateTime=" + dateTime);

                    List<Player> players = new ArrayList<>();
                    NodeList playerNodes = gameMoveElement.getElementsByTagName("player");
                    for (int j = 0; j < playerNodes.getLength(); j++) {
                        Node playerNode = playerNodes.item(j);
                        if (playerNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element playerElement = (Element) playerNode;



                            List<Card> hand = new ArrayList<>();
                            NodeList cardNodes = playerElement.getElementsByTagName("card");
                            for (int k = 0; k < cardNodes.getLength(); k++) {
                                Node cardNode = cardNodes.item(k);
                                if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element cardElement = (Element) cardNode;


                                    String cardStr = cardElement.getTextContent().trim();
                                    Card card = Card.fromString(cardStr);
                                    hand.add(card);
                                }
                            }

                            Player player = new Player(hand);
                            players.add(player);
                        }
                    }

                    List<Card> discardPile = new ArrayList<>();
                    NodeList discardNodes = gameMoveElement.getElementsByTagName("discardPile");
                    if (discardNodes.getLength() > 0) {
                        Element discardElement = (Element) discardNodes.item(0);


                        NodeList cardNodes = discardElement.getElementsByTagName("card");
                        for (int k = 0; k < cardNodes.getLength(); k++) {
                            Node cardNode = cardNodes.item(k);
                            if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element cardElement = (Element) cardNode;


                                String cardStr = cardElement.getTextContent().trim(); // Get text content without <card> tags
                                Card card = Card.fromString(cardStr);
                                discardPile.add(card);
                            }
                        }
                    }

                    Map<Suit, Integer> fireworks = new HashMap<>();
                    NodeList fireworksNodes = gameMoveElement.getElementsByTagName("fireworks");
                    if (fireworksNodes.getLength() > 0) {
                        Element fireworksElement = (Element) fireworksNodes.item(0);


                        NodeList suitNodes = fireworksElement.getElementsByTagName("suit");
                        NodeList countNodes = fireworksElement.getElementsByTagName("count");
                        for (int k = 0; k < suitNodes.getLength(); k++) {
                            if (suitNodes.item(k).getNodeType() == Node.ELEMENT_NODE &&
                                    countNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                Element suitElement = (Element) suitNodes.item(k);
                                Element countElement = (Element) countNodes.item(k);


                                String suitStr = suitElement.getTextContent().trim();
                                int count = Integer.parseInt(countElement.getTextContent().trim());
                                Suit suit = Suit.valueOf(suitStr); // Assuming Suit is an enum with valid constants
                                fireworks.put(suit, count);
                            }
                        }
                    }

                    GameMove gameMove = new GameMove(actionType, currentPlayerIndex, fuses, tips, dateTime, players, discardPile, fireworks);
                    gameMoves.add(gameMove);

                    System.out.println("Parsed gameMove: " + gameMove);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return gameMoves;
    }



    private static String getTextContent(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim(); // Assuming single text content for the tag
        }
        return "";
    }


    private static List<String> convertCardsToXmlStrings(List<Card> cards) {
        List<String> cardRepresentations = new ArrayList<>();
        for (Card card : cards) {

            String value = card.getCardNumber().toString();
            String suit = card.getCardSuit().toString();


            String cardRepresentation =  suit + " " + value;
            cardRepresentations.add(cardRepresentation);
        }
        return cardRepresentations;
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, element, null);
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(FILE_NAME)));
    }

    public static void createNewReplayFile() {
        try {
            Document document = createDocument("gameMoves");
            saveDocument(document, FILE_NAME);
        } catch (ParserConfigurationException | TransformerException ex) {
            ex.printStackTrace();
        }
    }
}
