import java.util.*;

public class Scanner {
	//original by Albert Geissbauer (GitHub @AlbGei), commented for easier understanding.

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final Deque<Character> charsQueue = new ArrayDeque<>();


    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanLine(String line, int lineNumber) {
        List<Token> returnToken = new ArrayList<>();

        char[] charArray = line.toCharArray();
        
        for (int i = 0; i < charArray.length; i++) {
            charsQueue.addLast(charArray[i]);
        }

        while (!charsQueue.isEmpty()) {
            if (charsQueue.getFirst() == ' ') {
                //remove white spaces from beginning of read line.
            	charsQueue.removeFirst();
            }

            //not ideal implementation. Maybe simplify? 
            if (charsQueue.getFirst() == '/') {
                charsQueue.removeFirst();
                if (charsQueue.getFirst() == '/') {
                    charsQueue.removeFirst();
                    StringBuilder stringBuilder = new StringBuilder();
                    while (!charsQueue.isEmpty()) {
                        //read rest of line into stringBuilder
                    	stringBuilder.append(charsQueue.getFirst());
                        charsQueue.removeFirst();
                    }
                    // Line with '//' beginning must be comment.
                    returnToken.add(new Token(TokenType.COMMENT, stringBuilder.toString(), stringBuilder.toString(), lineNumber));
                    break;
                } else {
                	//add second '/' if necessary.
                    charsQueue.addFirst('/');
                }
            }

            if (charsQueue.isEmpty()) {
                break;
            }
            
            returnToken.add(checkToken(lineNumber));

        }

        if (returnToken.contains(null)) {
            List<Token> temp = new ArrayList<>(returnToken);
            returnToken.clear();
            for (Token t : temp) {
                if (t != null) {
                    returnToken.add(t);
                }
            }
        }

        return returnToken;
    }

    public List<Token> scan() {
        String[] lines = source.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(scanLine(lines[i], i));
        }
        
        tokens.add(new Token(TokenType.EOF, "", "", lines.length));
        return tokens;
    }
    

    private Token checkToken(int lineNumber) {
        for (TokenType tokenType : TokenType.values()) {
            //check for literals.
        	if (tokenType != TokenType.IDENTIFIER && tokenType != TokenType.STRING &&  tokenType != TokenType.NUMBER && tokenType != TokenType.COMMENT && tokenType != TokenType.EOF  && compare(tokenType)) {
                return new Token(tokenType, tokenType.getRegex(), tokenType.getRegex(), lineNumber);
            }
        }

        //check for start of String initialization.
        if (charsQueue.getFirst() == '"') {
            StringBuilder stringBuilder = new StringBuilder();
            charsQueue.removeFirst();
            while (charsQueue.getFirst() != '"') {
                //stringBuilder with content of String.
            	stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            charsQueue.removeFirst();
            return new Token(TokenType.STRING, stringBuilder.toString(), stringBuilder.toString(), lineNumber);
        }

        
        //Check for numbers.
        if ((charsQueue.getFirst() <= 57 && charsQueue.getFirst() >= 48) || (charsQueue.getFirst() == '.')) {
            StringBuilder stringBuilder = new StringBuilder();
            while (!charsQueue.isEmpty() && ((charsQueue.getFirst() <= 57 && charsQueue.getFirst() >= 48) || (charsQueue.getFirst() == '.'))) {
                stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            return generateTokenNumber(stringBuilder.toString(), lineNumber);
        }

        //check for literals.
        if ((charsQueue.getFirst() <= 89 && charsQueue.getFirst() >= 65) || (charsQueue.getFirst() <= 122 && charsQueue.getFirst() >= 97)) {
            StringBuilder stringBuilder = new StringBuilder();
            while (!charsQueue.isEmpty() && ((charsQueue.getFirst() <= 89 && charsQueue.getFirst() >= 65) || (charsQueue.getFirst() <= 122 && charsQueue.getFirst() >= 97))) {
                stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            return new Token(TokenType.IDENTIFIER, stringBuilder.toString(), stringBuilder.toString(), lineNumber);
        }
        return null;
    }

    private boolean compare(TokenType tokenType) {
        char[] multipleChars = tokenType.getRegex().toCharArray();
        char[] charsToCheck = new char[multipleChars.length];

        if (charsQueue.size() < multipleChars.length)
            return false;

        for (int i = 0; i < multipleChars.length; i++) {
            charsToCheck[i] = charsQueue.getFirst();
            charsQueue.removeFirst();
        }
    	for(int i = 0; i < charsToCheck.length; i++) {
            if (charsToCheck[i] != multipleChars[i++]) {
                for (char cs : reverseCharArray(charsToCheck)) {
                    charsQueue.addFirst(cs);
                }
                return false;
            }
        }
        return true;
    }

    //reverse incoming array of chars. Could be implemented directly.
    private char[] reverseCharArray(char[] cArr) {
        char[] reversed = new char[cArr.length];
        int j = cArr.length - 1;
        for (char c : cArr) {
            reversed[j--] = c;
        }
        return reversed;
    }

    private Token generateTokenNumber(String str, int lineNumber) {
        //check if int or floating point number.
    	if (str.contains(".")) {
            return new Token(TokenType.NUMBER, str, Double.valueOf(str), lineNumber);
        } else {
            return new Token(TokenType.NUMBER, str, Integer.valueOf(str), lineNumber);
        }
    }
}
