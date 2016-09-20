package ru.youthsongs;

public class Song {
    // Название песни.
    private String _name;

    // Текст песни.
    private String _text;

    // Номер песни.
    private int _number;

    // Английское название песни (если есть).
    private String _enName;

    // Авторы песни (если есть).
    private String _authors;

    // Альтарнативное название песни (если есть).
    private String __altName;

    // Геттер/сеттер для названия песни.
    public String getName()
    {
        return  _name;
    }

    public void setName(String name)
    {
        if (name != null) {
            name = _name;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // Геттер/сеттер для текста песни.
    public String getText()
    {
        return  _text;
    }

    public void setText(String text)
    {
        if (text != null) {
            _text = text;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // Геттер/сеттер для номера песни.
    public int getNumber()
    {
        return _number;
    }

    public void setNumber(int number)
    {
        if (number != 0) {
            _number = number;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // Геттер/сеттер для английского названия песни.
    public String getEnName()
    {
        return _enName;
    }

    public void setNumber(String enName)
    {
        if (enName != null) {
            _enName = enName;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // Геттер/сеттер для авторов песни.
    public String getAuthors()
    {
        return _authors;
    }

    public void setAuthors(String Authors)
    {
        if (Authors != null) {
            _authors = Authors;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // Геттер/сеттер для альт. названия песни.
    public String getAltName()
    {
        return __altName;
    }

    public void setAltName (String altName)
    {
        if (altName != null) {
            __altName = altName;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
