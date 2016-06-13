(function($j) {

  $j.fn.radiologyDataTable = function(options) {
    var defaults = {
      "processing": true,
      "serverSide": true,
      "ajax": {
        headers: {
          Accept: "application/json; charset=utf-8",
          "Content-Type": "text/plain; charset=utf-8",
        },
        cache: true,
        dataType: "json",
      },
      "searching": false,
      "ordering": false,
    };
    var settings = $j.extend(defaults, options);
    console.log(settings);
    this.DataTable(settings);
    return this;
  }
}(jQuery));