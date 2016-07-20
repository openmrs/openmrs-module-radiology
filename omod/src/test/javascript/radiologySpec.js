describe(
        "Radiology module",
        function() {

          describe("getRestRootEndpoint", function() {
            it("should return the basis rest url", function() {
              expect(Radiology.getRestRootEndpoint()).toBe(
                      '/openmrs/ws/rest/v1');
            });
          });
          describe(
                  "getProperty",
                  function() {

                    it(
                            "should return the object property with a property which is defined and not null",
                            function() {
                              var full = {
                                orderReason: {
                                  uuid: "uuid",
                                  display: "FRACTURE"
                                }
                              }
                              expect(
                                      Radiology.getProperty(full,
                                              "orderReason.uuid")).toBe('uuid');
                            });

                    it(
                            "should return an empty string with a property which is undefined or null",
                            function() {
                              var full = {
                                orderReason: {
                                  uuid: undefined,
                                  display: null
                                }
                              }
                              expect(
                                      Radiology.getProperty(full,
                                              "orderReason.display")).toBe('');
                              expect(
                                      Radiology.getProperty(full,
                                              "orderReason.uuid")).toBe('');
                            });

                    it(
                            "should return an empty string when an object property is undefined or null",
                            function() {
                              var full = {
                                orderReason: null
                              }
                              expect(
                                      Radiology.getProperty(full,
                                              "orderReason.display")).toBe('');
                            });

                    it(
                            "should return an empty string when the object is undefined or null",
                            function() {
                              var full = null;
                              expect(
                                      Radiology.getProperty(full,
                                              "orderReason.display")).toBe('');
                            });
                  });

        });
