package client.utility;

import client.App;
import common.data.*;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.MustBeNotEmptyException;
import common.exceptions.NotInDeclaredLimitsException;
import common.utility.Outputer;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Asks user a marine's value.
 */
public class MarineAsker {
    private Scanner userScanner;
    private boolean fileMode;

    public MarineAsker(Scanner userScanner) {
        this.userScanner = userScanner;
        fileMode = false;
    }

    /**
     * Sets marine asker mode to 'File Mode'.
     */
    public void setFileMode() {
        fileMode = true;
    }

    /**
     * Sets marine asker mode to 'User Mode'.
     */
    public void setUserMode() {
        fileMode = false;
    }

    /**
     * Asks a user the marine's name.
     *
     * @return Marine's name.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public String askName() throws IncorrectInputInScriptException {
        String name;
        while (true) {
            try {
                Outputer.println("Enter a name:");
                Outputer.print(App.PS2);
                name = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(name);
                if (name.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("The name was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (MustBeNotEmptyException exception) {
                Outputer.printerror("The name cannot be empty!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                Outputer.printerror("Unexpected error occurred!");
                System.exit(0);
            }
        }
        return name;
    }

    /**
     * Asks a user the marine's X coordinate.
     *
     * @return Marine's X coordinate.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public double askX() throws IncorrectInputInScriptException {
        String strX;
        double x;
        while (true) {
            try {
                Outputer.println("Enter the coordinateX:");
                Outputer.print(App.PS2);
                strX = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(strX);
                x = Double.parseDouble(strX);
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("The X coordinate is not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                Outputer.printerror("The X coordinate must be represented by a number!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Outputer.printerror("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * Asks a user the marine's Y coordinate.
     *
     * @return Marine's Y coordinate.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Float askY() throws IncorrectInputInScriptException {
        String strY;
        Float y;
        while (true) {
            try {
                Outputer.println("Введите координату Y < " + (SpaceMarine.MAX_Y + 1) + ":");
                Outputer.print(App.PS2);
                strY = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(strY);
                y = Float.parseFloat(strY);
                if (y > SpaceMarine.MAX_Y) throw new NotInDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("The Y coordinate is not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                Outputer.printerror("Координата Y не может превышать " + SpaceMarine.MAX_Y + "!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                Outputer.printerror("Координата Y должна быть представлена числом!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Outputer.printerror("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return y;
    }

    /**
     * Asks a user the marine's coordinates.
     *
     * @return Marine's coordinates.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Coordinates askCoordinates() throws IncorrectInputInScriptException {
        double x;
        Float y;
        x = askX();
        y = askY();
        return new Coordinates(x, y);
    }

    /**
     * Asks a user the marine's health.
     *
     * @return Marine's health.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public double askHealth() throws IncorrectInputInScriptException {
        String strHealth;
        double health;
        while (true) {
            try {
                Outputer.println("Enter health:");
                Outputer.print(App.PS2);
                strHealth = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(strHealth);
                health = Double.parseDouble(strHealth);
                if (health <= SpaceMarine.MIN_HEALTH) throw new NotInDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("Health was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                Outputer.printerror("Health must be greater than zero!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                Outputer.printerror("Health must be represented by a number!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Outputer.printerror("Unexpected error!");
                System.exit(0);
            }
        }
        return health;
    }

    /**
     * Asks a user the marine's category.
     *
     * @return Marine's category.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public AstartesCategory askCategory() throws IncorrectInputInScriptException {
        String strCategory;
        AstartesCategory category;
        while (true) {
            try {
                Outputer.println("List of categories - " + AstartesCategory.nameList());
                Outputer.println("Enter the category:");
                Outputer.print(App.PS2);
                strCategory = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(strCategory);
                category = AstartesCategory.valueOf(strCategory.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("Category was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalArgumentException exception) {
                Outputer.printerror("The category is not in the list!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                Outputer.printerror("Unexpected error!");
                System.exit(0);
            }
        }
        return category;
    }

    /**
     * Asks a user the marine's weapon type.
     *
     * @return Marine's weapon type.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Weapon askWeaponType() throws IncorrectInputInScriptException {
        String strWeaponType;
        Weapon weaponType;
        while (true) {
            try {
                Outputer.println("List of ranged weapons - " + Weapon.nameList());
                Outputer.println("Enter the ranged weapon:");
                Outputer.print(App.PS2);
                strWeaponType = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(strWeaponType);
                weaponType = Weapon.valueOf(strWeaponType.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("Weapon was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalArgumentException exception) {
                Outputer.printerror("The weapon is not in the list!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                Outputer.printerror("Unexpected error!");
                System.exit(0);
            }
        }
        return weaponType;
    }

    /**
     * Asks a user the marine's melee weapon.
     *
     * @return Marine's melee weapon.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public MeleeWeapon askMeleeWeapon() throws IncorrectInputInScriptException {
        String strMeleeWeapon;
        MeleeWeapon meleeWeapon;
        while (true) {
            try {
                Outputer.println("List of melee weapons - " + MeleeWeapon.nameList());
                Outputer.println("Enter the melee weapon:");
                Outputer.print(App.PS2);
                strMeleeWeapon = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(strMeleeWeapon);
                meleeWeapon = MeleeWeapon.valueOf(strMeleeWeapon.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("Weapon was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalArgumentException exception) {
                Outputer.printerror("The weapon is not in the list!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                Outputer.printerror("Unexpected error!");
                System.exit(0);
            }
        }
        return meleeWeapon;
    }

    /**
     * Asks a user the marine chapter's name.
     *
     * @return Chapter's name.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public String askChapterName() throws IncorrectInputInScriptException {
        String chapterName;
        while (true) {
            try {
                Outputer.println("Enter the name of the chapter:");
                Outputer.print(App.PS2);
                chapterName = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(chapterName);
                if (chapterName.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("Chapter name was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (MustBeNotEmptyException exception) {
                Outputer.printerror("Chapter name cannot be empty!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                Outputer.printerror("Unexpected error!");
                System.exit(0);
            }
        }
        return chapterName;
    }

    /**
     * Asks a user the marine chapter's number of soldiers.
     *
     * @return Number of soldiers.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public long askChapterMarinesCount() throws IncorrectInputInScriptException {
        String strMarinesCount;
        long marinesCount;
        while (true) {
            try {
                Outputer.println("Enter the number of soldiers in the order < " + (SpaceMarine.MAX_MARINES + 1) + ":");
                Outputer.print(App.PS2);
                strMarinesCount = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(strMarinesCount);
                marinesCount = Long.parseLong(strMarinesCount);
                if (marinesCount < SpaceMarine.MIN_MARINES || marinesCount > SpaceMarine.MAX_MARINES)
                    throw new NotInDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("The number of soldiers in the order was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                Outputer.printerror("The number of soldiers in the order must be positive and not exceed " + SpaceMarine.MAX_MARINES + "!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                Outputer.printerror("The number of soldiers in the order must be represented by a number!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Outputer.printerror("Unexpected error!");
                System.exit(0);
            }
        }
        return marinesCount;
    }

    /**
     * Asks a user the marine's chapter.
     *
     * @return Marine's chapter.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Chapter askChapter() throws IncorrectInputInScriptException {
        String name;
        long marinesCount;
        name = askChapterName();
        marinesCount = askChapterMarinesCount();
        return new Chapter(name, marinesCount);
    }

    /**
     * Asks a user a question.
     *
     * @param question A question.
     * @return Answer (true/false).
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public boolean askQuestion(String question) throws IncorrectInputInScriptException {
        String finalQuestion = question + " (+/-):";
        String answer;
        while (true) {
            try {
                Outputer.println(finalQuestion);
                Outputer.print(App.PS2);
                answer = userScanner.nextLine().trim();
                if (fileMode) Outputer.println(answer);
                if (!answer.equals("+") && !answer.equals("-")) throw new NotInDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("Answer was not recognized!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                Outputer.printerror("The answer should be represented by '+' or '-' signs!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                Outputer.printerror("Unexpected error!");
                System.exit(0);
            }
        }
        return answer.equals("+");
    }
}
