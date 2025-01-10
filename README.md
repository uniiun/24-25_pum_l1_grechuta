# **Ballin' – Interaktywna ścieżka projektu**

## **Milestone 1: Inicjalizacja projektu**
- [x] Utwórz nowy projekt w Android Studio.
- [x] Skonfiguruj minimalny SDK i dependencies:
  - [x] `CameraX`
  - [x] `Room` lub SQLite
  - [x] `SensorManager`
- [ ] Utwórz pliki strukturalne:
  - [ ] `MainActivity`
  - [ ] `GameActivity`
  - [ ] `LevelManager`
- [ ] Przygotuj ekran startowy z prostym tekstem ("Ballin' Start").

---

## **Milestone 2: Mechanika sterowania**
- [ ] Odczytuj dane z żyroskopu za pomocą API `SensorManager`.
- [ ] Stwórz klasę `Ball` z podstawowymi atrybutami:
  - [ ] Pozycja.
  - [ ] Prędkość.
- [ ] Zaimplementuj ruch kulki w ograniczonym obszarze (Canvas/SurfaceView).
- [ ] Dodaj podstawową grawitację (kulka spada i odbija się od ścian).

---

## **Milestone 3: System poziomów – wstępna struktura**
- [ ] Zaprojektuj strukturę poziomów (JSON lub SQLite).
- [ ] Stwórz klasę `Level`:
  - [ ] Wymiary planszy.
  - [ ] Pozycja startowa kulki.
  - [ ] Lista przeszkód.
  - [ ] Cel (pozycja końcowa).
- [ ] Zaimplementuj `LevelManager` do ładowania poziomów z JSON.
- [ ] Utwórz testowy poziom.

---

## **Milestone 4: Detekcja kolizji i przeszkody**
- [ ] Dodaj przeszkody:
  - [ ] Prostokątne przeszkody.
  - [ ] Okrągłe przeszkody.
- [ ] Zaimplementuj detekcję kolizji:
  - [ ] Prostokąt – kulka.
  - [ ] Okrąg – kulka.
- [ ] Dodaj reakcję kulki na kolizję (odbicie pod odpowiednim kątem).

---

## **Milestone 5: Prototyp UI i poziomy**
- [ ] Stwórz prototypowy layout menu głównego:
  - [ ] Przycisk "Start".
  - [ ] Przycisk "Ustawienia".
  - [ ] Przycisk "Wyjście".
- [ ] Stwórz layout poziomu gry (placeholdery dla elementów graficznych).
- [ ] Dodaj kilka poziomów testowych (np. 3 poziomy o rosnącym stopniu trudności).

---

## **Milestone 6: Ruchome przeszkody i logika poziomów**
- [ ] Dodaj ruchome przeszkody:
  - [ ] Przeszkody poruszające się po linii prostej.
  - [ ] Kontrolowanie prędkości ruchu przeszkód.
- [ ] Sprawdź poprawność kolizji kulki z ruchomymi przeszkodami.
- [ ] Dodaj logikę poziomów:
  - [ ] Sprawdzenie, czy kulka osiągnęła cel.
  - [ ] Przejście do następnego poziomu.

---

## **Milestone 7: Integracja grafiki i animacji**
- [ ] Dodaj grafikę kulki (np. `.png`).
- [ ] Zamień przeszkody na obrazy.
- [ ] Dodaj animacje:
  - [ ] Ruch kulki.
  - [ ] Ruch przeszkód.

---

## **Milestone 8: System poziomów – finalizacja**
- [ ] Dodaj finalne poziomy do gry (np. 10 poziomów o różnej trudności).
- [ ] Przenieś poziomy do SQLite, jeśli JSON nie jest wystarczający.
- [ ] Testuj poziomy pod kątem balansu grywalności.

---

## **Milestone 9: Wibracje i efekty wizualne**
- [ ] Dodaj wibracje podczas kolizji (API Vibrator).
- [ ] Dodaj efekty wizualne (np. zmiana koloru tła przy wygranej).
- [ ] Zintegruj filtry obrazu na tle kamery.

---

## **Milestone 10: Optymalizacja i publikacja**
- [ ] Testuj wydajność na różnych urządzeniach.
- [ ] Optymalizuj ładowanie poziomów i przetwarzanie obrazu.
- [ ] Przygotuj finalne layouty, ikonę aplikacji i zasoby graficzne.
- [ ] Publikuj aplikację w Google Play Store.
