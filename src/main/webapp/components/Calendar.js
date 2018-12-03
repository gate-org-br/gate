function Calendar(month)
{
    var result = new Array();
    var ini = getFDOW(getFDOM(month));
    var end = getLDOW(getLDOM(month));
    for (var date = ini; date <= end; date.setDate(date.getDate() + 1))
        result.push(new Date(date));
    while (result.length < 42)
    {
        result.push(new Date(date));
        date.setDate(date.getDate() + 1);
    }
    return result;

    function getFDOM(date)
    {
        var fdom = new Date(date);
        fdom.setDate(1);
        return fdom;
    }

    function getLDOM(date)
    {
        var ldom = new Date(date);
        ldom.setMonth(ldom.getMonth() + 1);
        ldom.setDate(0);
        return ldom;
    }

    function getFDOW(date)
    {
        var fdow = new Date(date);
        while (fdow.getDay() !== 0)
            fdow.setDate(fdow.getDate() - 1);
        return fdow;
    }

    function getLDOW(date)
    {
        var ldow = new Date(date);
        while (ldow.getDay() !== 6)
            ldow.setDate(ldow.getDate() + 1);
        return ldow;
    }
}