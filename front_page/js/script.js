/**
 * Created by zhangshiqiu on 2017/4/17.
 */


function autocomplet() {
    var min_length = 0;
    var reg = '[^A-Za-z]';
    keyword = $('#q').val();
    if (keyword.length > min_length) {
        $.ajax({
            url: 'ajax_fresh.php/?',
            type: 'GET',
            data: {q:keyword},
            success:function(result){
                data = JSON.parse(result);
                list = data.suggest.suggest[keyword].suggestions;
                list = list.sort(sortWords);

                autocompletionlist = new Array();
                for (var i = 0; i < list.length; i++) {
                    var word = list[i].term;
                    if (autocompletionlist.length >= 5){
                        break;
                    }
                    if (word.search(reg) != -1 || word.length > 25){
                        continue;
                    }


                    var str = "<li onclick='set_item(\"" + word +"\")'>"+ word + "</li>"
                    autocompletionlist.push(str);
                }

                if (autocompletionlist.length !== 0){
                    $('#autocompletion').show();
                    $('#autocompletion').html(autocompletionlist);
                }
            }
        });
    } else {
        $('#autocompletion').hide();
    }
}

function sortWords(a, b) {
    return b.weight - a.weight;
}

function set_item(item) {
    $('#q').val(item);
    $('#autocompletion').hide();
}
