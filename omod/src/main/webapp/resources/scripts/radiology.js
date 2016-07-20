var Radiology = (function(openmrsContextPath) {

  var getRestRootEndpoint = function() {
    return openmrsContextPath + '/ws/rest/v1';
  };

  /**
   * Get a (nested) property safely from an object.
   * 
   * @param object the object to search
   * @param property the property to be returned in dot notation style (ie"full.orderReason") 
   * 
   * @return the value of the property in question if existing or empty string otherwise
   */
  var getProperty = function(object, property) {
    var propertyNames = property.split(".");
    var length = propertyNames.length;
    var property = object;
    for (var i = 0; i < length; i++) {
      if (hasProperty(property, propertyNames[i])) {
        property = property[propertyNames[i]];
      } else {
        return '';
      }
    }
    return isDefinedAndNotNull(property) ? property : '';
  };

  var isDefinedAndNotNull = function(property) {
    if ((typeof (property) !== 'undefined') && (property !== null)) {
      return true;
    } else {
      return false;
    }
  };

  var hasProperty = function(object, property) {
    if (isDefinedAndNotNull(object) && object.hasOwnProperty(property)) {
      return true;
    } else {
      return false;
    }
  };

  return {
    getRestRootEndpoint: getRestRootEndpoint,
    getProperty: getProperty
  };

})(openmrsContextPath);