# Dengue

## Usage

Use [android studio](https://developer.android.com/studio/index.html).

## Structure

Aaccording to [android studio projects Overview](https://developer.android.com/studio/projects/index.html)

#### Breeding Source

孳生源回報

#### Drugbite

蚊子咬回報

#### Guild

導覽畫面

#### Report

回報審核點清單

#### User

使用者相關

#### Hospital

醫院清單

#### Hot

熱區 web view

#### Session

Use to save data.

- setData(String key, String value)
    - key: key of data
    - value: value of data
- getData(String key)
    - key: key of data
    - It will return value.
- Example

    ```android
    string AppName = "dengue";
    session Session = new session(getSharedPreferences(AppName, 0));

    session.setData("isLogin", "true");
    // it will save isLogin as "true".
    session.getData("isLogin");
    // it will return "true"
    ```

#### GPS

Use to get address name.

- get(double lat, double lon)
    - lat: double type.
    - lon: double type.
- Example

    ```android
    // "this" is android activity.
    gps Gps = new Gps(this);
    Gps.get(0.0, 0.0);
    // it will return a name of [0, 0]
    ```

## Issue

- [ ] 未來要修改介面的話，在未登入都情況下按下個人設定按鈕，再按註冊下面的小字進入登入頁面，如果使用者他其實是未註冊的，現在想要退回去可能不知道怎麼操作比較好
