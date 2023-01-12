package pl.jsieczczynski.SpringBootRedditClone.utils;

public class Helpers {
    public static String makeId(int length) {
        StringBuilder result = new StringBuilder();
        String character = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int characterLength = character.length();
        for (int i = 0; i < length; i++) {
            int index = (int) (characterLength * Math.random());
            result.append(character.charAt(index));
        }
        return result.toString();
    }

    public static String slugify(String input) {
        // https://gist.github.com/codeguy/6684588#gistcomment-2759673
        String result = input.trim().toLowerCase();

        String from = "åàáãäâèéëêìíïîòóöôùúüûñç·/_,:;";
        String to = "aaaaaaeeeeiiiioooouuuunc------";

        for (int i = 0; i < from.length(); i++) {
            result = result.replace(from.charAt(i), to.charAt(i));
        }

        return result
                .replaceAll("[^a-z0-9\\s-]", "") // remove invalid chars
                .replaceAll("\\s+", "-") // collapse whitespace and replace by -
                .replaceAll("-+", "-") // collapse dashes
                .replaceAll("^-+", "") // trim - from start of text
                .replaceAll("-+$", "") // trim - from end of text
                .replaceAll("-", "_");
    }
}
