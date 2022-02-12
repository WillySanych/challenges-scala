##Решение проблемы обедающих философов

Задана модель поведение философов, при которой взять, положить вилку, есть и думать это разные действия.

Имеются 2 алгоритма работы: с блокировкой и без.
Блокировка происходит тогда, когда философ взял левую вилку и в случае, если правая вилка занята, то он не кладет левую вилку обратно, а ждет освобождения правой.
Когда таких философов становится несколько, то никто не способен взять две вилки и начать есть.

Программа работает 20 секунд, после чего происходит выход по таймауту.

##Makefile:
make build - сборка программы;

make run-non-blocking - запуск программы без взаимной блокировки;

make run-blocking - запуск программы с взаимной блокировкой.