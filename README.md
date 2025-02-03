Programowanie urządzeń mobilnych laboratorium L_1

# Dokumentacja projetu: Gra logiczna Ballin

## Zespoł projetowy:
Paweł Grechuta

## Opis projektu
Ballin' to gra logiczno-zręcznościowa, w której gracz musi przemierzać kolejne światy sterując ich grawitacją za pomocą żyroskopu telefonu. Światy (poziomy) gry reagują na otaczający gracza świat rzeczywisty poprzez czujniki telefonu.

## Zarys fabuły
Benson, znany w swoim świecie jako Ballin' Bounce, to legendarna kulka i prawdziwy mistrz akrobatyki. Jest kaskaderem, który podbija serca widzów swoimi niesamowitymi wyczynami. Podczas swojego ostatniego, transmitowanego na cały świat wydarzenia, sponsorowanego przez gigantyczną markę Red Ball, Benson podjął się próby pobicia rekordu świata w najwyższym odbiciu od ziemi.
Niestety, coś poszło nie tak – jego skok był tak potężny, że przebił granice rzeczywistości i wylądował w tajemniczym, malutkim wymiarze. Benson musi teraz eksplorować ten dziwny świat, pełen niezwykłych mostów.

## Zakres projektu opis funkcjonalności:
 **Sterowanie za pomocą żyroskopu**
- Gra wykorzystuje wbudowany w urządzenie sensor żyroskopowy do sterowania ruchem postaci.
- Dane z sensora są odczytywane i przetwarzane w czasie rzeczywistym, umożliwiając płynne i intuicyjne sterowanie.
- Ruch kulki w grze jest proporcjonalny do nachylenia urządzenia, co pozwala na precyzyjne manewrowanie po planszy.

 **Personalizacja koloru postaci**
- Gra oferuje interaktywny ekran ustawień, gdzie gracz może wybrać kolor Bensona z wykorzystaniem palety RGB lub predefiniowanych zestawów kolorów.
- Wybrany kolor jest zapisywany lokalnie w *SharedPreferences*, dzięki czemu ustawienia są zachowywane między sesjami.
- Zmiana koloru jest natychmiastowa i widoczna w czasie rzeczywistym w interfejsie gry.

 **Zmienny świat bazujący na obrazie z kamery**
- Gra integruje obraz z tylnej kamery urządzenia jako dynamiczne tło dla poziomów.
- Wideo z kamery jest przechwytywane i przepuszczane przez niestandardowy filtr obrazu.
- Przetwarzanie obrazu odbywa się w czasie rzeczywistym przy minimalnym opóźnieniu, zapewniając płynną animację tła.

 **Cykl dnia i nocy bazujący na czujniku światła**
- Gra wykorzystuje wbudowany w urządzenie sensor oświetlenia (Light Sensor), aby odczytywać natężenie światła otoczenia.
- Na podstawie wartości natężenia światła gra dynamicznie zmienia kolorystykę tła, symulując różne pory dnia:
  - **Wysokie natężenie światła** → Jasne tło.
  - **Niskie natężenie światła** → Ciemne tło.
- Zmiany te są płynne i zależne od rzeczywistych warunków otoczenia, co zwiększa immersję.

**Obsługa muzyki oraz efektów dźwiękowych**
- Gra reaguje na kolizje gracza z przeszkodami
- Podczas przechodzenia poziomów graczowi przygrywa muzyka

**Obsługa najlepszych wyników**
- Na ekranie wyboru poziomów obok każdego przycisku znajduje się najlepszy czas jego przejścia
- Podczas pauzy czas nie jest liczony

## Panele / zakładki aplikacji 
- Menu główne <br /> <br />
![menu](https://github.com/user-attachments/assets/f3308bb2-ffa0-4f59-98de-6629b6358639)


- Wybór poziomów <br /> <br />
![level-select](https://github.com/user-attachments/assets/1812ca52-5145-4056-816c-d3bf8565c64c)


- Zmiana koloru Bensona <br /> <br />
![color-select](https://github.com/user-attachments/assets/b4b9dd52-5469-4762-bc10-c8b8f8dc1d96)


- Ekran gry <br /> <br />
![game](https://github.com/user-attachments/assets/dfbac168-583a-4aed-8ddf-2c36d95d635b)
![game-cam](https://github.com/user-attachments/assets/440e402b-9a7d-4a47-8b6b-663bd43c7e3f)


- Menu pauzy <br /> <br />
![pause](https://github.com/user-attachments/assets/6597b85d-0323-4530-a066-b7fa156edd8b)


## Baza danych
Gra ze względu na niewielką ilość przetwarzanych danych wykorzystuje *SharedPreferences* przechowujące najlepsze wyniki oraz wybraną przez gracza postać

## Wykorzystane uprawnienia aplikacji do:
-Aparat - do wyświetlania obrazu z kamery jako dynamicznego tła.<br />
-Żyroskop - do sterowania bohaterem<br />
-Czujnik światła - do symulacji cyklu dnia i nocy na podstawie jasności otoczenia.<br />

