from rest_framework import serializers

class RequestSerializer(serializers.Serializer):
    d1 = serializers.IntegerField(default = 0)
    d2 = serializers.IntegerField(default = 0)
