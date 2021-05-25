package ru.lebedeva.memorycard.domain

/**
 * Класс для отображения состояния ответов с репозитория (чаще всего)
 *
 * @param T тип хранящихся данных
 * @property data данные
 * @property msg сообщение ошибки
 * @property error ошибка
 */
sealed class Resource<out T>(
    val data: T? = null,
    var msg: String? = null,
    val error: Throwable? = null
) {

    /**
     * Класс, отражающий состояние "Успешно"
     *
     * @param T
     * @constructor
     *
     * @param data данные
     */
    class Success<T>(
        data: T? = null
    ) : Resource<T>(data = data)

    /**
     * Объект, отражающий состояние "Загрузка"
     *
     */
    object Loading : Resource<Unit>()

    /**
     * Класс, отражающий состояние "Ошибка"
     *
     * @param T
     * @constructor
     *
     * @param error ошибка
     * @param msg сообщение ошибки
     * @param data данные
     */
    class Error<T>(
        error: Throwable? = null,
        msg: String? = null,
        data: T? = null
    ) : Resource<T>(data = data, msg = msg, error = error)
}
