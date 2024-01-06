package me.RonanCraft.BetterClaims.resources.files;

import lombok.Getter;

public class Files {
    @Getter private final FileLanguage lang = new FileLanguage();
    private final FileOther other = new FileOther();

    //To call a file just grab the enum with FileOther.FILETYPE.<file>

    /*public FileOther.FILETYPE getType(FileOther.FILETYPE type) {
        return other.types.get(other.types.indexOf(type));
    }*/

    public void loadAll() {
        other.load();
        lang.load();
    }
}
