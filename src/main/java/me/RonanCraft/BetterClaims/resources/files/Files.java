package me.RonanCraft.BetterClaims.resources.files;

public class Files {
    private final FileLanguage lang = new FileLanguage();
    private final FileOther other = new FileOther();

    public FileLanguage getLang() {
        return lang;
    }

    //To call a file just grab the enum with FileOther.FILETYPE.<file>

    /*public FileOther.FILETYPE getType(FileOther.FILETYPE type) {
        return other.types.get(other.types.indexOf(type));
    }*/

    public void loadAll() {
        other.load();
        lang.load();
    }
}
