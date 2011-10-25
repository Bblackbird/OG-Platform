/*
 * @copyright 2009 - present by OpenGamma Inc
 * @license See distribution for license
 */
$.register_module({
    name: 'og.common.details.timeseries_functions',
    dependencies: [],
    obj: function () {
        var timeseries_functions,
            /**
             * TODO: create generic date transformer
             * @param {String} selector
             * @param {json} data
             */
            render_table = function (selector, json, handler) {
                var CHUNK = 500, length = json.data.length, $parent = $(selector).html(
                       '<thead>\
                          <tr>\
                            <td>Data Points</td>\
                            <td><span class="OG-link og-js-timeSeriesCsv">Download (CSV)</span></td>\
                          </tr><tr>\
                            <td></td>\
                            <td></td>\
                          </tr>\
                        </thead>\
                        <tbody class="og-mod-content" style="height: 200px"></tbody>'
                    ).find('tbody'),
                    render = function (start, end) {
                        handler();
//                        if (start >= length) return handler();
//                        var str = json.data.slice(start, end).reduce(function (acc, val) {
//                            var date = new Date(val[0]), d = date.getDate(), m = date.getMonth() + 1,
//                                day = d < 10 ? '0' + d : d,
//                                month = m < 10 ? '0' + m : m;
//                            acc.push(
//                                '<tr><td>', day, '/', month, '/', date.getFullYear(), '</td>',
//                                '<td>', val[1], '</td></tr>'
//                            );
//                            return acc;
//                        }, []).join('');
//                        $parent.append(str);
//                        setTimeout(render.partial(end, end + CHUNK), 0);
                    };
                render(0, CHUNK);
            },
            render_identifiers = function (selector, json) {
                var html = [];
                json.forEach(function (datum) {
                    var date_text = '', start_date = datum.date.start, end_date = datum.date.end;
                    date_text = [end_date && (start_date || '?') || start_date, end_date].filter(Boolean).join(' - ');
                    html.push(
                        '<div><div>', datum.scheme.replace('_', ' '), ':</div><div><strong>', datum.value,
                        '</strong><span>', (date_text && '(' + date_text + ')'), '</span></div></div>'
                    );
                });
                $(selector).html(html.join(''));
            }

        return timeseries_functions = {
            render_table: render_table,
            render_identifiers: render_identifiers
        };

    }
});