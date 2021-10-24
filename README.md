# Курс основ программирования на МКН СПбГУ
## Проект 3: визуализация данных

Это проект первого курса СПбГУ факультета МКН "Современное программирование" — программа, рисующая диаграмму одного из доступных типов в графическом окне по входным данным (из файла или из консоли), с записью изображения в PNG файл (при необходимости). Проект использует библиотеки [skiko](https://github.com/JetBrains/skiko) и [skija](https://github.com/JetBrains/skija). Оригинальная постановка задачи: [TASK.md](./TASK.md).

## Запуск
В релизах можно найти скомпилированный jar файл `viz.jar`. Запуск программы производится следующей командой:
```sh
java -jar viz.jar [-i INPUT_FILE] [SORT_FLAG] [-d DIAGRAM_TYPE] [-s SCALE] [-o OUTPUT_FILE]
```
Все аргументы необязательны; порядок аргументов не имеет значения.

Запустить также можно непосредственно в среде разработки, указав нужные опции и аргументы.

## Поддерживаемые типы диаграмм

### Столбчатая диаграмма (гистограмма)
<p align='center'>
<img src = 'https://user-images.githubusercontent.com/39223464/137521422-29a8d5ea-81d8-4f61-bf20-7aa91e64b4aa.png' height="320px">
</p>

Можно вызвать, указав опции ``-d bar``, ``-d column`` или ``-d histogram``. Является диаграммой по умолчанию (она будет вызвана, если вообще не указывать опцию ``-d``) Записи с отрицательными значениями запрещены.

### Диаграмма-линия (график)
<p align='center'>
<img src = 'https://user-images.githubusercontent.com/39223464/137522025-72559865-5e91-431c-b79a-429cb2bf901b.png' height="320px">
</p>

Можно вызвать, указав опции ``-d line``, ``-d graph``, ``-d plot`` или ``-d curve``. Ось значений не обязательно начнается с нуля: диапазон подбирается автоматически под данные.

### График с областями
<p align='center'>
<img src = 'https://user-images.githubusercontent.com/39223464/138570367-de57576a-e9fa-46b4-8a65-5d9831f35363.png' height="320px">
</p>

Можно вызвать, указав опции ``-d area``, ``-d fill``. Записи с отрицательными значениями запрещены.

### Круговая диаграмма
<p align='center'>
<img src = 'https://user-images.githubusercontent.com/39223464/137522451-b0f5f254-b188-4d47-a943-4b9ea1cdf7cb.png' height="320px">
</p>

Можно вызвать, указав опции ``-d pie``, ``-d circle`` или ``-d round``. Поддерживает максимум десять различных цветов: при совпадении цвета проводятся серые линии между надписями и соответствующими секторами диаграммы. Данные, содержащие отрицательные значения, а также данные, сумма значений которых равна нулю, запрещены.


## Входные данные

Опцией ``-i INPUT_FILE`` можно указать файл с данными для диаграммы. В случае, когда опция не использована, или когда файл не может быть найден или прочитан, данные считываются из консоли.

В любом случае формат входных данных следующий: каждая запись находится в отдельной строке, и состоит из строки, являющейся названием компонента диаграммы, и вещественного числа, которое отделено от названия пробелами. Строки, которые не могут быть распознаны как корректная запись, пропускаются.

При вводе данных из консоли ввод следует заканчивать комбинацией ``CTRL+D`` в Linux и `CTRL+Z` с последующим ``Enter`` в Windows.

## Упорядочивание данных
Следующими флагами можно задать порядок данных на диаграмме:
* ``--sort`` - упорядочить в порядке возрастания значений
* ``--rsort``, ``--reverse-sort`` - упорядочить в порядке убывания значений
* ``--lsort``, ``--lex-sort`` - упорядочить в порядке возрастания названий (лексикографически)
* ``--lrsort``, ``--rlsort``, ``--lex-reverse-sort``, ``--reverse-lex-sort`` - упорядочить в порядке убывания названий (лексикографически)
* ``--nsort``, ``--no-sort`` - оставить порядок, как есть (опция по умолчанию)


## Размер диаграммы

Опцией ``-s SCALE``, где ``SCALE`` - вещественное число, можно задать относительный размер диаграммы. Размер по умолчанию равен 400. Наименьший разрешенный размер равен 50, а наибольший равен 2000.

## Выходные данные

При корректном входе программа рисует диаграмму в графическом окне. Если использовать опцию ``-o OUTPUT_FILE``, то программа дополнительно сохранит диаграмму в указанный файл в формате PNG.

## Прочие опции
* ``--no-window`` - не создавать графическое окно с диаграммой.
* ``--silent``, ``--quiet`` - включить режим, при котором нет вывода в консоль.

## Примеры использования
Проверить программу вручную можно на примерах из папки `samples`.

## Коды возврата
+ 0 — программа выполнилась успешно
+ 1 — программа выполнилась с ошибками (некорректные опции/аргументы или некорректные данные для диаграммы)

## Тестирование
Исходный код сопровождён unit-тестами, покрывающими компоненты ввода и обработки данных. Кроме того, с помощью функций из класса ``src/test/kotlin/AllSamples.kt`` можно получить картинки сразу всех диаграмм из ``samples`` всех вомзожных типов и сравнить их с эталонными (но для этого нужно сначала сгенерировать эталонные картинки другой функцией).