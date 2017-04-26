<?php
header('Content-Type: text/html; charset=utf-8');
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;

if ($query) {
    require_once('Apache/Solr/Service.php');

    $solr = new Apache_Solr_Service('localhost', 8983, '/solr/my_solr/');

    if (get_magic_quotes_gpc() == 1) {
        $query = stripslashes($query);
    }

    try {
        $additionalParameters = array(
            'fl' => 'title,og_url,id,description',
            'sort' => $_REQUEST['sort']
        );
        $results = $solr->search($query, 0, $limit, $additionalParameters);
    } catch (Exception $e) {
        die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
    }
}

?>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Solr Client</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="css/style.css">
    <script
            src="https://code.jquery.com/jquery-3.2.1.min.js"
            integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
            crossorigin="anonymous"></script>
    <script src="js/script.js"></script>
</head>
<body class="container" style="padding-top: 30px">

<form class="form-horizontal" accept-charset="utf-8" method="get">
    <div class="input_container">
        <label for="q" class="col-sm-2 control-label">Search</label>
        <div class="col-sm-10">
            <input class="form-control" autocomplete="off" id="q" name="q" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" onkeyup="autocomplet()" type="text">
            <ul id="autocompletion"></ul>
        </div>
    </div>
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <div class="radio">
                <label class="radio-inline">
                    <input type="radio" name="sort" value="score desc" <?php if (!isset($_REQUEST['q']) || $_REQUEST['sort'] != 'pageRankFile desc') {echo "checked";}; ?>/>
                    TF-IDF
                </label>
                <label class="radio-inline">
                    <input type="radio" name="sort" value="pageRankFile desc" <?php if (isset($_REQUEST['q']) && $_REQUEST['sort'] == 'pageRankFile desc') {echo "checked";}; ?>/>
                    PageRank
                </label>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default">submit</button>
        </div>
    </div>
</form>
<?php

if ($results)
{
    $total = (int) $results->response->numFound;
    $start = min(1, $total);
    $end = min($limit, $total);

    include 'SpellCorrector.php';
    $spellCorrection = SpellCorrector::correct($query);
    ?>

    <?php
if ($spellCorrection != $query){
    ?>

   <h3>
       <a href=<?php echo "\"?q=".$spellCorrection."&sort=".$_REQUEST['sort']."\""; ?>>
           Are you looking for <u><?php echo $spellCorrection; ?></u>
       </a>
   </h3>

    <?php
}
?>

    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ul class="list-group">
        <?php
        $file = fopen("/Users/zhangshiqiu/572/mapLATimesDataFile.csv","r");
        while ($data = fgetcsv($file)) {
            $dict[$data[0]] = $data[1];
        }
        fclose($file);

        foreach ($results->response->docs as $doc) {
            ?>
            <li class="list-group-item">
                <table class="table table-bordered">
                    <?php
                        $url = $doc->getField('og_url')["value"];
                        if (is_null($url)){
                            $id = $doc->getField('id')["value"];
                            $sid = substr($id, 34);
                            $url = $dict[$sid];
                        }
                    ?>
                    <tr class="row">
                        <th class="col-md-2">id</th>
                        <td class="col-md-10">
                            <a href=<?php echo "\"".$url."\""; ?>>
                                <?php echo htmlspecialchars($doc->getField('id')["value"], ENT_NOQUOTES, 'utf-8'); ?>
                            </a>
                        </td>
                    </tr>
                    <tr class="row">
                        <th class="col-md-2">title</th>
                        <td class="col-md-10">
                            <a href=<?php echo "\"".$url."\""; ?>>
                            <?php echo htmlspecialchars($doc->getField('title')["value"], ENT_NOQUOTES, 'utf-8'); ?>
                            </a>
                        </td>
                    </tr>
                    <tr class="row">
                        <th class="col-md-2">description</th>
                        <td class="col-md-10">
                            <?php echo htmlspecialchars($doc->getField('description')["value"], ENT_NOQUOTES, 'utf-8'); ?>
                        </td>
                    </tr>
                    <tr class="row">
                        <th class="col-md-2">url</th>
                        <td class="col-md-10">
                            <a href=<?php echo "\"".$url."\""; ?>>
                                <?php echo htmlspecialchars($url, ENT_NOQUOTES, 'utf-8'); ?>
                            </a>
                        </td>
                    </tr>
                    <tr class="row">
                        <th class="col-md-2">snippet</th>
                        <td class="col-md-10">
                            <?php
                            $contentFile = $doc->getField('id')["value"];
                            $contentFile = str_replace("crawelData", "contentFiles", $contentFile);
                            $contentFile = str_replace("html", "txt", $contentFile);
                            $content = file_get_contents($contentFile);
                            if (strlen(trim($content)) == 0){
                                $content = $doc->getField('description')["value"];
                            }
                            $content = " ".$content." ";

                            $content = preg_replace("/([\\W]+)(".$query.")/i", "$1<mark><u><strong>$2</strong></u></mark>", $content);

                            $index = stripos($content, $query);
                            if ($index){
                                $start = max(0, $index - 60);

                                $length = min(500, strlen($content) - $start);
                                $info = "...".substr($content, $start, $length)."...";

                                echo $info;
                            }

                            ?>
                        </td>
                    </tr>
                </table>
            </li>
            <?php
        }
        ?>
    </ul>
    <?php
}
?>
</body>
</html>